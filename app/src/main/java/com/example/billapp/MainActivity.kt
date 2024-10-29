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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
            var isDialogVisible2 by remember { mutableStateOf(true) }
            val reminderSummary by viewModel.reminderSummary.collectAsState()

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
                                viewModel.checkReminders(userId)
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
                reminderSummary.let { summary ->
                    if (summary.totalAmount > 0 && isDialogVisible2) {
                        Log.d(TAG, "有債務提醒 : ${summary.totalAmount}")
                        DebtReminderDialog(
                            reminderSummary = summary,
                            onDismiss = {
                                isDialogVisible2 = false
                                scope.launch {
                                    viewModel.markRemindersAsRead(summary.reminders)
                                }
                            }
                        )
                    }else
                    {
                        Log.d(TAG, "沒有債務提醒")
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
        modifier = Modifier.border(
            width = 1.dp,
            color = Color(0xFFFF5244),
            shape = RoundedCornerShape(16.dp)
        ),
        title = {
            Text(
                text = "債務提醒!!!",
                color = PrimaryFontColor,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                // 摘要資訊區塊
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "您目前有 ${reminderSummary.count} 筆未償還債務",
                            color = PrimaryFontColor,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "總金額：${reminderSummary.totalAmount} 元",
                            color = Color(0xFFD32F2F),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 詳細清單標題
                Text(
                    text = "詳細清單",
                    color = PrimaryFontColor,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // 債務詳細列表
                reminderSummary.reminders.forEach { reminder ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "・${reminder.creditorName}",
                            color = PrimaryFontColor,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${reminder.amount} 元",
                            color = Color(0xFFD32F2F),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F)
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    "確認",
                    color = Color.White
                )
            }
        },
        containerColor = MainBackgroundColor,
        shape = RoundedCornerShape(16.dp)
    )
}

@Preview
@Composable
fun DebtReminderDialogPreview() {
    val remainderSummary = ReminderSummary(1, 100.0, emptyList())
    DebtReminderDialog(remainderSummary) {
    }
}
