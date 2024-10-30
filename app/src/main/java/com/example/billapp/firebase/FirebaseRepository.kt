package com.example.billapp.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import com.example.billapp.MainActivity
import com.example.billapp.R
import com.example.billapp.ReminderSummary
import com.example.billapp.data.models.Achievement
import com.example.billapp.data.models.Badge
import com.example.billapp.data.models.DebtRelation
import com.example.billapp.data.models.DebtReminder
import com.example.billapp.data.models.Group
import com.example.billapp.data.models.GroupTransaction
import com.example.billapp.data.models.PersonalTransaction
import com.example.billapp.data.models.User
import com.example.billapp.utils.Constants
import com.google.android.gms.nearby.connection.AuthenticationException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

object FirebaseRepository {

    private fun getFirestoreInstance() = FirebaseFirestore.getInstance()
    private fun getAuthInstance() = FirebaseAuth.getInstance()

    private const val TAG = "FirebaseRepository"
    suspend fun signIn(email: String, password: String): User {
        try {
            Log.d(TAG, "Starting sign in process for email: ${email.maskEmail()}")

            // 1. 執行登入
            val authResult = getAuthInstance().signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID is null after sign in")
            Log.d(TAG, "Successfully signed in user: ${userId.take(5)}...")

            // 2. 獲取 FCM Token
            val token = try {
                FirebaseMessaging.getInstance().token.await()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get FCM token", e)
                throw e
            }
            Log.d(TAG, "Successfully retrieved FCM token")

            // 3. 更新用戶的 FCM Token
            try {
                getFirestoreInstance().collection(Constants.USERS)
                    .document(userId)
                    .update("fcmToken", token)
                    .await()
                Log.d(TAG, "Successfully updated FCM token in Firestore")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update FCM token in Firestore", e)
                throw e
            }

            // 4. 獲取用戶資料
            val userDoc = try {
                getFirestoreInstance().collection(Constants.USERS)
                    .document(userId)
                    .get()
                    .await()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch user data", e)
                throw e
            }

            return userDoc.toObject(User::class.java)
                ?: throw Exception("Failed to parse user data")
        } catch (e: Exception) {
            Log.e(TAG, "Sign in process failed", e)
            throw e
        }
    }

    // 用於日誌記錄時遮罩電子郵件地址的擴展函數
    private fun String.maskEmail(): String {
        val parts = this.split("@")
        if (parts.size != 2) return this
        val name = parts[0]
        val domain = parts[1]
        val maskedName = when {
            name.length <= 2 -> name
            name.length <= 4 -> name.take(1) + "*".repeat(name.length - 1)
            else -> name.take(2) + "*".repeat(name.length - 2)
        }
        return "$maskedName@$domain"
    }

    fun signOut() {
        getAuthInstance().signOut()
    }

    suspend fun signUp(name: String, email: String, password: String): User = suspendCoroutine { continuation ->
        getAuthInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = getAuthInstance().currentUser
                    val user = User(firebaseUser!!.uid, name, email)
                    getFirestoreInstance().collection(Constants.USERS)
                        .document(user.id)
                        .set(user)
                        .addOnSuccessListener {
                            continuation.resume(user)
                        }
                        .addOnFailureListener { e ->
                            continuation.resumeWithException(e)
                        }
                    updateUserFCMToken(firebaseUser.uid)
                } else {
                    continuation.resumeWithException(task.exception ?: Exception("Sign up failed"))
                }
            }
    }

    fun updateUserBudget(budget: Int) {
        val currentUser = getAuthInstance().currentUser
        if (currentUser != null) {
            getFirestoreInstance().collection(Constants.USERS)
                .document(currentUser.uid)
                .update("budget", budget)
        }
    }

    // achievement //

    // Achievement functions
    fun getAllAchievements(): Flow<List<Achievement>> = callbackFlow {
        val userId = getAuthInstance().currentUser?.uid ?: return@callbackFlow
        val subscription = getFirestoreInstance().collection("users")
            .document(userId)
            .collection("achievements")
            .addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            val achievements = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Achievement::class.java)?.let {
                    Achievement(
                        title = it.title,
                        currentCount = it.currentCount,
                        targetCount = it.targetCount,
                        color = Color(it.color).toArgb()
                    )
                }
            } ?: emptyList()
            trySend(achievements)
        }
        awaitClose { subscription.remove() }
    }

    suspend fun updateAchievementProgress(id: String, count: Int,userId: String) {
        getFirestoreInstance().collection("users")
            .document(userId)
            .collection("achievements")
            .document(id)
            .update(
                mapOf(
                    "currentCount" to count,
                    "lastUpdated" to Timestamp.now()
                )
            ).await()
    }

    // Badge functions
    fun getAllBadges(userId: String): Flow<List<Badge>> = callbackFlow {

        val subscription = getFirestoreInstance().collection("users")
            .document(userId)
            .collection("badges")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                val badges = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Badge::class.java)?.let {
                        Badge(
                            id = it.id,
                            name = it.name,
                            iconName = it.iconName,
                            unlocked = it.unlocked,
                            currentProgress = it.currentProgress,
                            maxProgress = it.maxProgress
                        )
                    }
                } ?: emptyList()
                trySend(badges)
            }
            awaitClose { subscription.remove() }
    }

    fun getUnlockedBadges(userId: String): Flow<List<Badge>> = getAllBadges(userId).map { badges ->
        badges.filter { it.unlocked }
    }

    suspend fun updateBadgeProgress(id: String, progress: Float,userId: String) {
        val badge = getFirestoreInstance().collection("users")
            .document(userId)
            .collection("badges")
            .document(id)
            .get()
            .await()
            .toObject(Badge::class.java)
        if (badge != null) {
            val isNewlyUnlocked = !badge.unlocked && progress >= badge.maxProgress
            val updates = mutableMapOf<String, Any>(
                "currentProgress" to progress,
                "lastUpdated" to Timestamp.now()
            )
            if (isNewlyUnlocked) {
                updates["unlocked"] = true
                updates["unlockedDate"] = Timestamp.now()
            }
            getFirestoreInstance().collection("users")
                .document(userId)
                .collection("badges")
                .document(id).update(updates).await()
        }
    }

    // Initial data setup
    suspend fun initializeAchievementsIfEmpty(userId: String) {
        val snapshot = getFirestoreInstance().collection("users")
            .document(userId)
            .collection("achievements")
            .limit(1)
            .get()
            .await()
        if (snapshot.isEmpty) {
            val defaultAchievements = listOf(
                Achievement(id = "record_master", title = "記帳達人", description = "累積記帳 100 次", currentCount = 0, targetCount = 100, color = Color.Blue.toArgb()),
                Achievement(id = "debt_master", title = "分帳達人", description = "累積完成 50 次分帳", currentCount = 0, targetCount = 50, color = Color.Green.toArgb()),
                Achievement(id = "trust_streak", title = "誠信保持者", description = "保持信任度 100% 超過 30 天", currentCount = 0, targetCount = 30, color = Color.Yellow.toArgb())
            )
            defaultAchievements.forEach { achievement ->
                getFirestoreInstance().collection("users")
                    .document(userId)
                    .collection("achievements")
                    .document(achievement.id).set(achievement).await()
            }
        }
    }

    suspend fun initializeBadgesIfEmpty(userId: String) {
        val snapshot = getFirestoreInstance().collection("users")
            .document(userId)
            .collection("badges")
            .limit(1).get().await()
        if (snapshot.isEmpty) {
            val defaultBadges = getDefaultBadges()
            defaultBadges.forEach { badge ->
                getFirestoreInstance().collection("users")
                    .document(userId)
                    .collection("badges")
                    .document(badge.id).set(badge).await()
            }
        }
    }

    private fun getDefaultBadges(): List<Badge> = listOf(
        Badge(
            id = "first_split_badge",
            name = "初次分帳",
            description = "首次與好友完成分帳",
            iconName = "handshake_icon",
            currentProgress = 0f,
            maxProgress = 1f
        ),
        Badge(
            id = "savings_badge",
            name = "節儉大師",
            description = "連續 30 天支出少於 1000 元",
            iconName = "piggy_bank_icon",
            currentProgress = 0f,
            maxProgress = 30f
        ),
        Badge(
            id = "social_star_badge",
            name = "社交新星",
            description = "達到社交等級 3",
            iconName = "star_icon",
            currentProgress = 0f,
            maxProgress = 3f
        ),
        Badge(
            id = "trust_shield_badge",
            name = "信用超人",
            description = "信任度保持 100% 超過 15 天",
            iconName = "shield_icon",
            currentProgress = 0f,
            maxProgress = 15f
        ),
        Badge(
            id = "debt_hero_badge",
            name = "還款俠客",
            description = "快速還清超過 10 筆債務",
            iconName = "sword_icon",
            currentProgress = 0f,
            maxProgress = 10f
        ),
        Badge(
            id = "accounting_badge",
            name = "記帳達人",
            description = "累積記帳 100 次",
            iconName = "accounting_icon",
            currentProgress = 0f,
            maxProgress = 100f,
            unlocked = false
        ),
        Badge(
            id = "trust_master_badge",
            name = "人見人愛",
            description = "信任度保持 100% 超過一個月",
            iconName = "trust_icon",
            currentProgress = 0f,
            maxProgress = 30f,
            unlocked = false
        ),
        Badge(
            id = "quick_debt_clear_badge",
            name = "快速清帳",
            description = "欠款後一天內還款三次",
            iconName = "quick_clear_icon",
            currentProgress = 0f,
            maxProgress = 3f,
            unlocked = false
        ),
        Badge(
            id = "social_master_badge",
            name = "社交高手",
            description = "創建 5 個群組並在每個群組完成一筆分帳交易",
            iconName = "social_master_icon",
            currentProgress = 0f,
            maxProgress = 5f,
            unlocked = false
        ),
        Badge(
            id = "savings_master_badge",
            name = "省錢達人",
            description = "連續記錄 30 天支出並保持結餘為正",
            iconName = "saving_icon",
            currentProgress = 0f,
            maxProgress = 30f,
            unlocked = false
        ),
        Badge(
            id = "accounting_streak_badge",
            name = "全勤記帳王",
            description = "連續記帳 7 天",
            iconName = "streak_icon",
            currentProgress = 0f,
            maxProgress = 7f,
            unlocked = false
        )
    )

    ////

    private const val GROUPS_COLLECTION = "groups"
    private const val DEBT_RELATIONS_COLLECTION = "debtRelations"

    suspend fun updateDebtLastPenaltyDate(groupId: String, debtId: String, timestamp: Long) {
        try {
            val db = FirebaseFirestore.getInstance()
            val debtRelationRef = db.collection(GROUPS_COLLECTION)
                .document(groupId)
                .collection(DEBT_RELATIONS_COLLECTION)
                .document(debtId)

            // 使用 transaction 確保數據一致性
            db.runTransaction { transaction ->
                val snapshot = transaction.get(debtRelationRef)
                if (snapshot.exists()) {
                    Log.d("FirebaseRepository", "找到債務關係文檔，準備更新懲罰日期。債務ID: $debtId") // 確認是否找到匹配的文檔

                    val updates = hashMapOf<String, Any>(
                        "lastPenaltyDate" to Timestamp(timestamp / 1000, 0) // 將毫秒轉換為秒
                    )
                    transaction.update(debtRelationRef, updates)
                } else {
                    Log.w("FirebaseRepository", "未找到債務關係文檔，無法更新。債務ID: $debtId")
                }
            }.await()

            Log.d("FirebaseRepository", "成功更新債務最後懲罰日期。群組ID: $groupId, 債務ID: $debtId")
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "更新債務最後懲罰日期時發生錯誤", e)
            throw e
        }
    }

    fun updateUserFCMToken(userId: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                getFirestoreInstance().collection(Constants.USERS).document(userId)
                    .update("fcmToken", token)
            }
        }
    }

    //////

    suspend fun checkReminders(userId: String): ReminderSummary {
        try {
            val unreadReminders = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("reminders")
                .whereEqualTo("read", false)
                .get()
                .await()
                .toObjects(DebtReminder::class.java)

            Log.d(TAG, "檢查提醒，找到 ${unreadReminders.size} 條未讀提醒")

            if (unreadReminders.isEmpty()) {
                return ReminderSummary(0, 0.0, emptyList())
            }

            val totalAmount = unreadReminders.sumOf { it.amount }
            return ReminderSummary(
                count = unreadReminders.size,
                totalAmount = totalAmount,
                reminders = unreadReminders
            )
        } catch (e: Exception) {
            Log.e(TAG, "檢查提醒失敗", e)
            return ReminderSummary(0, 0.0, emptyList())
        }
    }

    suspend fun markRemindersAsRead(userId: String, reminders: List<DebtReminder>) {
        val batch = FirebaseFirestore.getInstance().batch()

        reminders.forEach { reminder ->
            val reminderRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("reminders")
                .document(reminder.id)

            batch.update(reminderRef, "read", true)
            Log.d(TAG, "Updating reminder: ${reminder.id} to read = true")
        }

        try {
            batch.commit().await()
            Log.d(TAG, "已標記 ${reminders.size} 條提醒為已讀")
        } catch (e: Exception) {
            Log.e(TAG, "標記提醒為已讀失敗", e)
        }
    }

    suspend fun sendDebtReminder(context: Context, debtRelation: DebtRelation) = withContext(Dispatchers.IO) {
        try {
            val currentDate = Timestamp.now()

            if (debtRelation.lastRemindTimestamp == null || canSendReminder(currentDate, debtRelation.lastRemindTimestamp)) {
                // 獲取債權人資訊
                val creditorDoc = getFirestoreInstance()
                    .collection("users")
                    .document(debtRelation.to)
                    .get()
                    .await()

                val creditorName = creditorDoc.getString("name") ?: "未知用戶"
                val reminderId = UUID.randomUUID().toString()

                // 建立提醒資訊
                val reminder = DebtReminder(
                    id = reminderId,
                    debtRelationId = debtRelation.id,
                    amount = debtRelation.amount,
                    creditorId = debtRelation.to,
                    creditorName = creditorName,
                    createdAt = currentDate
                )

                // 儲存提醒到債務人的提醒集合中，並獲取自動生成的 ID
                val reminderRef = getFirestoreInstance()
                    .collection("users")
                    .document(debtRelation.from)
                    .collection("reminders")
                    .document(reminder.id)
                    .set(reminder)
                    .await()

                // 更新債務關係的最後提醒時間
                getFirestoreInstance()
                    .collection("debtRelations")
                    .document(debtRelation.id)
                    .update("lastRemindTimestamp", currentDate)
                    .await()

                // 更新用戶經驗值和信任等級
                updateUserExperience(debtRelation.to, 5)
                updateUserTrustLevel(debtRelation.from, -5)

                return@withContext true
            } else {
                return@withContext false
            }
        } catch (e: Exception) {
            Log.e("Reminder", "Error in sendDebtReminder", e)
            throw e
        }
    }


//    suspend fun sendDebtReminder(context: Context, debtRelation: DebtRelation) = withContext(Dispatchers.IO) {
//        try {
//            val currentDate = Timestamp.now()
//
//            if (debtRelation.lastRemindTimestamp == null || canSendReminder(currentDate, debtRelation.lastRemindTimestamp)) {
//                // 檢查接收者的 FCM token 是否存在
//                val receiverToken = getUserFCMToken(debtRelation.from)
//                if (receiverToken.isNullOrEmpty()) {
//                    throw Exception("Receiver's FCM token not found")
//                }
//
//                // 建立 FCM 訊息
//                val message = RemoteMessage.Builder(receiverToken)
//                    .setMessageId(UUID.randomUUID().toString())
//                    .setData(mapOf(
//                        "title" to "債務提醒",
//                        "message" to "您有一筆 ${debtRelation.amount} 元的債務需要償還給 ${debtRelation.to}",
//                        "userId" to debtRelation.from,
//                        "timestamp" to currentDate.seconds.toString()
//                    ))
//                    .build()
//
//                try {
//                    // 發送推送通知
//                    FirebaseMessaging.getInstance().send(message)
//
//                    // 更新最後提醒時間
//                    getFirestoreInstance()
//                        .collection("debtRelations")
//                        .document(debtRelation.id)
//                        .update("lastRemindTimestamp", currentDate)
//                        .await()
//
//                    // 更新用戶經驗值和信任等級
//                    updateUserExperience(debtRelation.to, 5)
//                    updateUserTrustLevel(debtRelation.from, -5)
//
//                    return@withContext true
//                } catch (e: Exception) {
//                    Log.e("FCM", "Error sending message", e)
//                    throw Exception("Failed to send notification: ${e.message}")
//                }
//            } else {
//                return@withContext false
//            }
//        } catch (e: Exception) {
//            Log.e("FCM", "Error in sendDebtReminder", e)
//            throw e
//        }
//    }

    private fun canSendReminder(currentDate: Timestamp, lastReminderDate: Timestamp): Boolean {
        val oneDayInMillis = 24 * 60 * 60 * 1000
        return currentDate.toDate().time - lastReminderDate.toDate().time >= oneDayInMillis
    }

    private suspend fun sendPushNotification(userId: String, title: String, message: String) {
        try {
            // 獲取用戶的 FCM token
            val userToken = getUserFCMToken(userId)

            if (userToken != null) {
                // Create data payload
                val remoteMessage = RemoteMessage.Builder(userToken)
                    .setMessageId(System.currentTimeMillis().toString())  // Unique message ID
                    .setData(mapOf(
                        "title" to title,
                        "message" to message,
                        "userId" to userId
                    ))  // Add custom data
                    .build()

                // Send the message
                FirebaseMessaging.getInstance().send(remoteMessage)
                Log.d("FCM", "Successfully sent message to user: $userId")
            } else {
                // 用戶 FCM token 不存在
                Log.e("FCM", "User token not found for userId: $userId")
            }
        } catch (e: Exception) {
            // 捕捉發送推送消息時的異常
            Log.e("FCM", "Error sending FCM message", e)
        }
    }


    private suspend fun getUserFCMToken(userId: String): String? {
        return try {
            val userDoc = getFirestoreInstance()
                .collection("users")
                .document(userId)
                .get()
                .await()

            userDoc.getString("fcmToken")?.also { token ->
                if (token.isEmpty()) {
                    Log.w("FCM", "Empty FCM token for user: $userId")
                }
            }
        } catch (e: Exception) {
            Log.e("FCM", "Error getting user FCM token", e)
            null
        }
    }

    //////


    suspend fun updateUserExperience(userId: String, amount: Int) {
        getFirestoreInstance().collection("users").document(userId).update("experience", FieldValue.increment(amount.toLong()))
    }

    suspend fun updateUserTrustLevel(userId: String, amount: Int) {
        getFirestoreInstance().collection("users").document(userId).update("trustLevel", FieldValue.increment(amount.toLong()))
    }

    suspend fun createGroup(group: Group): String = withContext(Dispatchers.IO) {
        val currentUser = getAuthInstance().currentUser ?: throw IllegalStateException("No user logged in")
        val groupId = getFirestoreInstance().collection(Constants.GROUPS).document().id
        val groupData = group.copy(
            createdBy = currentUser.uid,
            id = groupId,
            createdTime = Timestamp.now()
        )
        getFirestoreInstance().collection(Constants.GROUPS)
            .document(groupId)
            .set(groupData, SetOptions.merge())
            .await()
        return@withContext groupId
    }

    suspend fun getUserName(userId: String): String = withContext(Dispatchers.IO) {
        val user = getFirestoreInstance().collection(Constants.USERS)
            .document(userId)
            .get()
            .await()
            .toObject(User::class.java)
        return@withContext user?.name ?: "Unknown User"
    }


    suspend fun getCurrentUser(): User = withContext(Dispatchers.IO) {
        val currentUser = getAuthInstance().currentUser ?: throw IllegalStateException("No user logged in")
        getFirestoreInstance().collection("users").document(currentUser.uid).get().await().toObject(
            User::class.java)
            ?: throw IllegalStateException("User data not found")
    }

    suspend fun getUserLineToken(userId: String): String = withContext(Dispatchers.IO) {
        try {
            val user = getFirestoreInstance().collection(Constants.USERS)
                .document(userId)
                .get()
                .await()
                .toObject(User::class.java)

            return@withContext user?.lineToken ?: throw Exception("User or line token not found")
        } catch (e: Exception) {
            throw Exception("Failed to get user line token: ${e.message}")
        }
    }

    suspend fun updateUserLineToken(userId: String, lineToken: String) {
           getFirestoreInstance().collection(Constants.USERS).document(userId).update("lineToken", lineToken).await()
    }

    suspend fun getUserData(userId: String): User = withContext(Dispatchers.IO) {
        try {
            val userDocument = getFirestoreInstance().collection(Constants.USERS)
                .document(userId)
                .get()
                .await()

            if (userDocument.exists()) {
                return@withContext userDocument.toObject(User::class.java)
                    ?: throw Exception("Failed to convert document to User object")
            } else {
                throw Exception("User not found")
            }
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error getting user data", e)
            throw e
        }
    }

    suspend fun fetchGroupWithDebtRelations(groupId: String): Group? {
        return withContext(Dispatchers.IO) {
            try {
                // 获取主要的 group 文档
                val groupDoc = getFirestoreInstance()
                    .collection(Constants.GROUPS)
                    .document(groupId)
                    .get()
                    .await()

                val group = groupDoc.toObject(Group::class.java) ?: return@withContext null

                // 获取 debtRelations 子集合
                val debtRelationsSnapshot = groupDoc.reference
                    .collection("debtRelations")
                    .get()
                    .await()

                // 将子集合数据转换为 DebtRelation 对象列表
                val debtRelations = debtRelationsSnapshot.documents.mapNotNull { doc ->
                    doc.toObject(DebtRelation::class.java)
                }

                // 更新 group 对象的 debtRelations
                group.debtRelations.clear()
                group.debtRelations.addAll(debtRelations)

                Log.d("FirebaseRepository", "Group ${group.id} fetched with ${debtRelations.size} debt relations")

                return@withContext group
            } catch (e: Exception) {
                Log.e("FirebaseRepository", "Error fetching group with debt relations", e)
                return@withContext null
            }
        }
    }

    suspend fun getUserGroups(): List<Group> = withContext(Dispatchers.IO) {
        try {
            val currentUser =
                getAuthInstance().currentUser ?: throw IllegalStateException("No user logged in")
            val userId = currentUser.uid
            Log.d("FirebaseRepository", "開始獲取用戶群組，用戶ID: $userId")

            // Query for groups where the user is in assignedTo
            Log.d("FirebaseRepository", "查詢用戶被分配的群組")
            val assignedGroupsSnapshot = getFirestoreInstance()
                .collection(Constants.GROUPS)
                .whereArrayContains("assignedTo", userId)
                .orderBy("createdTime", Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d(
                "FirebaseRepository",
                "找到 ${assignedGroupsSnapshot.documents.size} 個被分配的群組"
            )
            val assignedGroups = assignedGroupsSnapshot.documents.mapNotNull { doc ->
                try {
                    // 首先輸出原始文檔數據
                    Log.d("FirebaseRepository", "群組 ${doc.id} 原始數據: ${doc.data}")

                    // 特別檢查 debtRelations 字段
                    val rawDebtRelations = doc.get("debtRelations")
                    Log.d(
                        "FirebaseRepository",
                        "群組 ${doc.id} 原始債務關係數據類型: ${rawDebtRelations?.javaClass?.name}"
                    )
                    Log.d(
                        "FirebaseRepository",
                        "群組 ${doc.id} 原始債務關係數據內容: $rawDebtRelations"
                    )

                    val group = doc.toObject(Group::class.java)
                    Log.d(
                        "FirebaseRepository",
                        "群組 ${doc.id} 轉換後的債務關係數量: ${group?.debtRelations?.size ?: 0}"
                    )

                    group
                } catch (e: Exception) {
                    Log.e("FirebaseRepository", "轉換群組 ${doc.id} 時出錯", e)
                    null
                }
            }

            // Query for groups where the user is the creator
            Log.d("FirebaseRepository", "查詢用戶創建的群組")
            val createdGroupsSnapshot = getFirestoreInstance()
                .collection(Constants.GROUPS)
                .whereEqualTo("createdBy", userId)
                .orderBy("createdTime", Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d("FirebaseRepository", "找到 ${createdGroupsSnapshot.documents.size} 個創建的群組")
            val createdGroups = createdGroupsSnapshot.documents.mapNotNull { doc ->
                try {
                    val group = doc.toObject(Group::class.java)
                    Log.d(
                        "FirebaseRepository",
                        "群組 ${doc.id} 轉換結果: ${group != null}, 債務關係數量: ${group?.debtRelations?.size ?: 0}"
                    )

                    // 檢查原始數據
                    val rawDebtRelations = doc.get("debtRelations")
                    Log.d(
                        "FirebaseRepository",
                        "群組 ${doc.id} 原始債務關係數據: $rawDebtRelations"
                    )

                    group
                } catch (e: Exception) {
                    Log.e("FirebaseRepository", "轉換群組 ${doc.id} 時出錯", e)
                    null
                }
            }

            // Combine both lists
            val allGroups = (assignedGroups + createdGroups)
                .distinctBy { it.id }
                .sortedByDescending { it.createdTime }

            // 为每个组获取完整的数据，包括 debtRelations
            val completeGroups = allGroups.mapNotNull { group ->
                fetchGroupWithDebtRelations(group.id)
            }

            Log.d("FirebaseRepository", "最終獲取到 ${completeGroups.size} 個完整的群组")
            completeGroups.forEachIndexed { index, group ->
                Log.d(
                    "FirebaseRepository",
                    "群组 ${index + 1}: ${group.name}, 債務關係數量: ${group.debtRelations.size}"
                )
            }

            updateUserGroupsID(userId, completeGroups.map { it.id })

            return@withContext completeGroups
        }catch (e: Exception) {
            Log.e("FirebaseRepository", "獲取用戶群組時發生錯誤", e)
            throw e
        }
    }

    private fun updateUserGroupsID(userId: String, map: List<String>) {
        getFirestoreInstance().collection("users").document(userId).update("groupsID", map)
    }

    suspend fun deleteGroup(groupId: String) = withContext(Dispatchers.IO) {
        getFirestoreInstance().collection("groups").document(groupId).delete().await()
    }

    suspend fun deletePersonalTransaction(transactionId: String, transactionType: String, transactionAmount: Double) = withContext(Dispatchers.IO) {
        val currentUser = getAuthInstance().currentUser ?: throw IllegalStateException("No user logged in")
        val userId = currentUser.uid

        // Reference to the transaction document
        val transactionRef = getFirestoreInstance().collection(Constants.USERS)
            .document(userId)
            .collection("transactions")
            .document(transactionId)

        // Delete the transaction document
        transactionRef.delete().await()

        // Update the user's total income or expense
        val userRef = getFirestoreInstance().collection(Constants.USERS).document(userId)
        when (transactionType) {
            "收入" -> userRef.update("income", FieldValue.increment(-transactionAmount)).await()
            "支出" -> userRef.update("expense", FieldValue.increment(-transactionAmount)).await()
            else -> Log.e("deletePersonalTransaction", "Invalid transaction type: $transactionType")
        }
    }

    suspend fun updateGroup(groupId: String, group: Group) = withContext(Dispatchers.IO) {
        getFirestoreInstance().collection(Constants.GROUPS).document(groupId).set(group).await()
    }

    suspend fun assignUserToGroup(groupId: String, userId: String) = withContext(Dispatchers.IO) {
        val groupRef = getFirestoreInstance().collection("groups").document(groupId)
        val group = groupRef.get().await().toObject(Group::class.java)
        val currentUser = getCurrentUser()
        currentUser.groupsID.add(groupId)
        group?.assignedTo?.add(userId)
        groupRef.set(group!!).await()
    }

    suspend fun getGroup(groupId: String): Group = withContext(Dispatchers.IO) {
        return@withContext getFirestoreInstance()
            .collection(Constants.GROUPS)
            .document(groupId)
            .get()
            .await()
            .toObject(Group::class.java) ?: throw IllegalStateException("Group not found")
    }

    suspend fun getGroupTransactions(groupId: String): List<GroupTransaction> = withContext(Dispatchers.IO) {
        return@withContext getFirestoreInstance()
            .collection(Constants.GROUPS)
            .document(groupId)
            .collection("transactions")
            .get()
            .await()
            .toObjects(GroupTransaction::class.java)
    }


    // 新增一筆個人交易紀錄
    suspend fun addPersonalTransaction(transaction: PersonalTransaction) = withContext(Dispatchers.IO) {
        val currentUser = getAuthInstance().currentUser ?: throw IllegalStateException("No user logged in")
        val userId = currentUser.uid

        // Generate a unique transactionId using Firestore's document ID
        val transactionId = getFirestoreInstance().collection(Constants.USERS)
            .document(userId)
            .collection("transactions")
            .document()
            .id

        // Create a transaction object with the generated transactionId
        val transactionWithId = transaction.copy(
            transactionId = transactionId,
            userId = userId
        )

        // Add the transaction to the user's transactions subcollection
        getFirestoreInstance().collection(Constants.USERS)
            .document(userId)
            .collection("transactions")
            .document(transactionId)
            .set(transactionWithId)
            .await()

        // Update the user's total income or expense
        val userRef = getFirestoreInstance().collection(Constants.USERS).document(userId)
        when (transaction.type) {
            "收入" -> userRef.update("income", FieldValue.increment(transaction.amount)).await()
            "支出" -> userRef.update("expense", FieldValue.increment(transaction.amount)).await()
            else -> Log.e("addPersonalTransaction", "Invalid transaction type: ${transaction.type}")
        }
    }

    // 取得個人交易紀錄
    suspend fun getUserTransactions(userId: String): List<PersonalTransaction> = withContext(Dispatchers.IO) {
        return@withContext getFirestoreInstance()
            .collection(Constants.USERS)
            .document(userId)
            .collection("transactions")
            .get()
            .await()
            .toObjects(PersonalTransaction::class.java)
    }

    suspend fun getTransaction(transactionId: String): PersonalTransaction = withContext(Dispatchers.IO) {
        val currentUser = getAuthInstance().currentUser ?: throw IllegalStateException("No user logged in")
        val userId = currentUser.uid
        return@withContext getFirestoreInstance()
            .collection(Constants.USERS)
            .document(userId)
            .collection("transactions")
            .document(transactionId)
            .get()
            .await()
            .toObject(PersonalTransaction::class.java)!!
    }

    // 新增一筆群組交易紀錄
    suspend fun addGroupTransaction(groupId: String, transaction: GroupTransaction, debtRelations: List<DebtRelation>) = withContext(Dispatchers.IO) {
        val transactionId = getFirestoreInstance().collection(Constants.GROUPS)
            .document(groupId)
            .collection("transactions")
            .document()
            .id

        val transactionWithId = transaction.copy(id = transactionId)

        // 添加交易
        getFirestoreInstance().collection(Constants.GROUPS)
            .document(groupId)
            .collection("transactions")
            .document(transactionId)
            .set(transactionWithId)
            .await()

        // 添加債務關係
        for (debtRelation in debtRelations) {
            debtRelation.groupTransactionId = transactionId
            getFirestoreInstance().collection(Constants.GROUPS)
                .document(groupId)
                .collection("debtRelations")
                .document(debtRelation.id)
                .set(debtRelation)
                .await()
        }
    }

    suspend fun getGroupDebtRelations(groupId: String): Map<String, List<DebtRelation>> = withContext(Dispatchers.IO) {
        val debtRelations = getFirestoreInstance().collection(Constants.GROUPS)
            .document(groupId)
            .collection("debtRelations")
            .get()
            .await()
            .toObjects(DebtRelation::class.java)

        return@withContext debtRelations.groupBy { it.groupTransactionId }
    }

    // 取得群組成員
    suspend fun getGroupMembers(groupId: String): List<User> = withContext(Dispatchers.IO) {
        val group = getGroup(groupId)
        val memberIds = group.assignedTo + group.createdBy
        return@withContext memberIds.mapNotNull { userId ->
            getFirestoreInstance().collection(Constants.USERS)
                .document(userId)
                .get()
                .await()
                .toObject(User::class.java)
        }
    }

    suspend fun updateGroupDebtRelations(groupId: String, debtRelations: List<DebtRelation>) = withContext(Dispatchers.IO) {
        getFirestoreInstance().collection(Constants.GROUPS)
            .document(groupId)
            .update("debtRelations", debtRelations)
            .await()
    }

    suspend fun deleteDebtRelation(groupId: String, debtRelationId: String) = withContext(Dispatchers.IO) {
        getFirestoreInstance().collection(Constants.GROUPS)
            .document(groupId)
            .collection("debtRelations")
            .document(debtRelationId)
            .delete()
    }

}