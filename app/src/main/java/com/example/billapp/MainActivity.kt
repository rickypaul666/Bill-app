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
import androidx.compose.material3.MaterialTheme
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
            setupDailyExperienceWork()
        }
        setContent {
            MaterialTheme (
                typography = custom_jf_Typography
            ) {
                MainScreen(
                    viewModel = viewModel,
                    avatarViewModel = avatarViewModel,
                    requestPermission = { permission ->
                        requestPermissionLauncher.launch(permission)
                    }
                )
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

