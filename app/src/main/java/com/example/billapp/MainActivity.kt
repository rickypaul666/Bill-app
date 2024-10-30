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
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.work.*
import com.example.billapp.home.MainScreen
import com.example.billapp.ui.theme.theme.ButtonRedColor
import com.example.billapp.ui.theme.theme.MainBackgroundColor
import com.example.billapp.ui.theme.theme.PrimaryFontColor
import com.example.billapp.ui.theme.theme.custom_jf_Typography
import com.example.billapp.viewModel.AvatarViewModel
import com.example.billapp.viewModel.MainViewModel
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

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

        Firebase.messaging.isAutoInitEnabled = true
        Log.d(TAG, "Firebase Messaging 自動初始化已啟用")

        Firebase.messaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d(TAG, "成功獲取FCM Token: $token")
            } else {
                Log.e(TAG, "獲取FCM Token失敗", task.exception)
            }
        }

        val notificationPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
        Log.d(TAG, "當前通知權限狀態: ${if (notificationPermissionGranted) "已授權" else "未授權"}")

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        installSplashScreen()

        setContent {
            var isDialogVisible by remember { mutableStateOf(true) }

            MaterialTheme(
                typography = custom_jf_Typography
            ) {
                val scope = rememberCoroutineScope()
                val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()

                LaunchedEffect(isUserLoggedIn) {
                    if (viewModel.isUserLoggedIn.value) {
                        Log.d(TAG, "用戶已登入，檢查債務關係")
                        scope.launch {
                            viewModel.loadUserData(viewModel.getCurrentUserID())
                            viewModel.checkUserDebtRelations(viewModel.getCurrentUserID())
                            setupDailyExperienceWork()
                            viewModel.initializeDefaultAchievements(viewModel.getCurrentUserID())
                        }
                    } else {
                        Log.d(TAG, "用戶未登入")
                    }
                }

                MainScreen(
                    viewModel = viewModel,
                    avatarViewModel = avatarViewModel,
                    requestPermission = { permission ->
                        requestPermissionLauncher.launch(permission)
                    }
                )

                val debtCount by viewModel.debtCount.collectAsState()
                val totalTrustPenalty by viewModel.totalTrustPenalty.collectAsState()

                LaunchedEffect(debtCount, totalTrustPenalty) {
                    Log.d(TAG, "債務數量: $debtCount, 信任懲罰: $totalTrustPenalty")
                }

                LaunchedEffect(debtCount, isDialogVisible) {
                    Log.d(
                        TAG,
                        "對話框顯示條件: debtCount > 0 (${debtCount > 0}) && isDialogVisible ($isDialogVisible)"
                    )
                }

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