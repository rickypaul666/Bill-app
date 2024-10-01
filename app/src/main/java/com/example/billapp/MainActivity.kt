// MainActivity.kt
package com.example.billapp

import DailyExperienceWorker
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.billapp.viewModel.AvatarViewModel
import com.example.billapp.viewModel.MainViewModel
import com.example.billapp.ui.theme.custom_jf_Typography
import java.util.concurrent.TimeUnit
import androidx.work.Constraints
import com.example.billapp.ui.theme.ButtonRedColor
import com.example.billapp.ui.theme.MainBackgroundColor
import com.example.billapp.ui.theme.PrimaryFontColor
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val avatarViewModel: AvatarViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d(TAG, "通知權限已獲得")
            } else {
                Log.w(TAG, "通知權限被拒絕")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d(TAG, "正在請求通知權限")
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            Log.d(TAG, "Android版本低於13，不需要請求通知權限")
        }

        // Initialize Firebase
        Firebase.messaging.isAutoInitEnabled = true
        Log.d(TAG, "Firebase Messaging 自動初始化已啟用")

        // Request the FCM token
        Firebase.messaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d(TAG, "成功獲取FCM Token: $token")
            } else {
                Log.e(TAG, "獲取FCM Token失敗", task.exception)
            }
        }

        // 檢查當前的通知權限狀態
        val notificationPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
        Log.d(TAG, "當前通知權限狀態: ${if (notificationPermissionGranted) "已授權" else "未授權"}")

        // 其餘代碼保持不變
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        installSplashScreen()
        if (viewModel.isUserLoggedIn.value) {
            Log.d(TAG, "用戶已登入，檢查債務關係")
            viewModel.checkUserDebtRelations(viewModel.getCurrentUserID())
            setupDailyExperienceWork()
        } else {
            Log.d(TAG, "用戶未登入")
        }
        setContent {
            MaterialTheme(
                typography = custom_jf_Typography
            ) {
                MainScreen(
                    viewModel = viewModel,
                    avatarViewModel = avatarViewModel,
                    requestPermission = { permission ->
                        requestPermissionLauncher.launch(permission)
                    }
                )

                // 觀察 debtCount 和 totalTrustPenalty
                val debtCount by viewModel.debtCount.collectAsState()
                val totalTrustPenalty by viewModel.totalTrustPenalty.collectAsState()

                // 添加日誌來追蹤這些值
                LaunchedEffect(debtCount, totalTrustPenalty) {
                    Log.d(TAG, "債務數量: $debtCount, 信任懲罰: $totalTrustPenalty")
                }

                // 管理對話框的顯示狀態
                var isDialogVisible by remember { mutableStateOf(true) }

                // 添加日誌來追蹤對話框顯示條件
                LaunchedEffect(debtCount, isDialogVisible) {
                    Log.d(
                        TAG,
                        "對話框顯示條件: debtCount > 0 (${debtCount > 0}) && isDialogVisible ($isDialogVisible)"
                    )
                }

                // 當 debtCount 大於 0 且 isDialogVisible 時顯示對話框
                if (debtCount > 0 && isDialogVisible) {
                    Log.d(TAG, "顯示債務提醒對話框")
                    DebtReminderDialog(
                        debtCount = debtCount,
                        totalTrustPenalty = totalTrustPenalty,
                        onDismiss = {
                            isDialogVisible = false
                            Log.d(TAG, "對話框被關閉，isDialogVisible 設為 false")
                        }
                    )
                }
            }
        }
    }
    private fun setupDailyExperienceWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyExperienceWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "dailyExperienceIncrease",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
        Log.d(TAG, "已設置每日經驗值更新工作")
    }
}

@Composable
fun DebtReminderDialog(debtCount: Int, totalTrustPenalty: Int, onDismiss: () -> Unit) {
    if (debtCount > 0) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "提醒",
                    color = PrimaryFontColor
                )
            },
            text = {
                Text(
                    text = "目前有 $debtCount 筆債務未償還，信任值扣除 $totalTrustPenalty 點",
                    color = PrimaryFontColor
                )
            },
            confirmButton = {
                TextButton(
                    onClick = onDismiss,
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                        contentColor = ButtonRedColor
                    )
                ) {
                    Text("確認")
                }
            },
            containerColor = MainBackgroundColor // 使用主背景色
        )
    }
}

