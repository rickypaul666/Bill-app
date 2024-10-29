package com.example.billapp

import DailyExperienceWorker
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.billapp.data.models.DebtReminder
import com.example.billapp.ui.theme.ButtonRedColor
import com.example.billapp.ui.theme.MainBackgroundColor
import com.example.billapp.ui.theme.PrimaryFontColor
import com.example.billapp.ui.theme.custom_jf_Typography
import com.example.billapp.viewModel.AvatarViewModel
import com.example.billapp.viewModel.MainViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val avatarViewModel: AvatarViewModel by viewModels()
    private val TAG = "MainActivity"

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d(TAG, "通知權限已獲得")
        } else {
            Log.w(TAG, "通知權限被拒絕")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 檢查通知權限
        checkNotificationPermission()

        // 設置螢幕方向
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // 安裝啟動畫面
        installSplashScreen()

        setContent {
            var isDialogVisible by remember { mutableStateOf(true) }
            var reminderSummary by remember { mutableStateOf<ReminderSummary?>(null) }

            MaterialTheme(typography = custom_jf_Typography) {
                val scope = rememberCoroutineScope()
                val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()

                // 監聽用戶登入狀態
                LaunchedEffect(isUserLoggedIn) {
                    if (isUserLoggedIn) {
                        Log.d(TAG, "用戶已登入，開始初始化")
                        scope.launch {
                            try {
                                val userId = viewModel.getCurrentUserID()

                                // 加載用戶數據
                                viewModel.loadUserData(userId)
                                viewModel.checkUserDebtRelations(userId)
                                setupDailyExperienceWork()
                                viewModel.initializeDefaultAchievements(userId)

                                // 檢查提醒
                                reminderSummary = viewModel.checkReminders(userId)
                            } catch (e: Exception) {
                                Log.e(TAG, "用戶數據初始化失敗", e)
                            }
                        }
                    } else {
                        Log.d(TAG, "用戶未登入")
                    }
                }

                // UI 組件
                MainScreen(
                    viewModel = viewModel,
                    avatarViewModel = avatarViewModel,
                    requestPermission = { permission ->
                        requestPermissionLauncher.launch(permission)
                    }
                )

                // 顯示用戶提醒對話框
                reminderSummary?.let { summary ->
                    if (summary.totalAmount > 0 && isDialogVisible) {
                        DebtReminderDialog(
                            reminderSummary = summary,
                            onDismiss = {
                                isDialogVisible = false
                                scope.launch {
                                    viewModel.markRemindersAsRead(summary.reminders)
                                }
                            }
                        )
                    }
                }

                // 顯示自動提醒對話框邏輯
                val debtCount by viewModel.debtCount.collectAsState()
                val totalTrustPenalty by viewModel.totalTrustPenalty.collectAsState()

                if (debtCount > 0 && isDialogVisible) {
                    AutodebtReminderDialog(
                        debtCount = debtCount,
                        totalTrustPenalty = totalTrustPenalty,
                        onDismiss = { isDialogVisible = false }
                    )
                }
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when (PackageManager.PERMISSION_GRANTED) {
                checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) -> {
                    Log.d(TAG, "已有通知權限")
                }
                else -> {
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }


    private fun setupDailyExperienceWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyExperienceWorker>(
            1, TimeUnit.DAYS
        ).setConstraints(constraints).build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "dailyExperienceIncrease",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
        Log.d(TAG, "已設置每日經驗值更新工作")
    }
}

@Composable
fun AutodebtReminderDialog(debtCount: Int, totalTrustPenalty: Int, onDismiss: () -> Unit) {
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
                    text = "目前有 $debtCount 筆債務未償還，信譽點數扣除 $totalTrustPenalty 點",
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
            containerColor = MainBackgroundColor
        )
    }
}

data class ReminderSummary(
    val count: Int,
    val totalAmount: Double,
    val reminders: List<DebtReminder>
)

@Composable
fun DebtReminderDialog(
    reminderSummary: ReminderSummary,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "債務提醒",
                color = PrimaryFontColor
            )
        },
        text = {
            Column {
                Text(
                    text = "您目前有 ${reminderSummary.count} 筆未償還債務",
                    color = PrimaryFontColor,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "總金額：${reminderSummary.totalAmount} 元",
                    color = PrimaryFontColor
                )
                // 顯示詳細的債務列表
                reminderSummary.reminders.forEach { reminder ->
                    Text(
                        text = "・欠款 ${reminder.amount} 元給 ${reminder.creditorName}",
                        color = PrimaryFontColor,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = ButtonRedColor
                )
            ) {
                Text("確認")
            }
        },
        containerColor = MainBackgroundColor
    )
}