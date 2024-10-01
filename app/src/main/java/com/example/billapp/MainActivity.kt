// MainActivity.kt
package com.example.billapp

import DailyExperienceWorker
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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


class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val avatarViewModel: AvatarViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // 權限獲得，可以進行相關操作
            } else {
                // 權限被拒絕，可以顯示一個解釋或提示
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        // Initialize Firebase
        Firebase.messaging.isAutoInitEnabled = true

        // Request the FCM token (optional if you want to do some operation with it)
        Firebase.messaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                // Do something with the token (e.g., send it to your server)
            } else {
                // Handle failure to retrieve token
            }
        }
        // 禁止螢幕旋轉
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        installSplashScreen()
        if (viewModel.isUserLoggedIn.value) {
            viewModel.checkUserDebtRelations(viewModel.getCurrentUserID())
            setupDailyExperienceWork()
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

                // 管理對話框的顯示狀態
                var isDialogVisible by remember { mutableStateOf(true) }

                // 當 debtCount 大於 0 且 isDialogVisible 時顯示對話框
                if (debtCount > 0 && isDialogVisible) {
                    DebtReminderDialog(
                        debtCount = debtCount,
                        totalTrustPenalty = totalTrustPenalty,
                        onDismiss = { isDialogVisible = false } // 關閉對話框
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

