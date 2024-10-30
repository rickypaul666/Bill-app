package com.example.billapp.Achievement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billapp.home.DETAILED_ACHIEVEMENTS_ROUTE
import com.example.billapp.home.DETAILED_BADGES_ROUTE
import com.example.billapp.viewModel.MainViewModel

@Composable
fun AchievementsScreen(
    viewModel: MainViewModel,
    navController: NavController,
    onNavigateBack: () -> Unit
) {
    val achievements by viewModel.achievements.collectAsState()
    val badges by viewModel.badges.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 頂部導航列
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
            }
            Text("成就系統", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(10.dp))
        }

        // 成就區塊
        AchievementsSection(
            achievements = achievements,
            onViewAllClick = { navController.navigate(DETAILED_ACHIEVEMENTS_ROUTE) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 徽章區塊
        BadgesSection(
            badges = badges,
            onViewAllClick = { navController.navigate(DETAILED_BADGES_ROUTE) }
        )

    }
}

