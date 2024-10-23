package com.example.billapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Egg
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalDining
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.billapp.data.models.Achievement
import com.example.billapp.data.models.Badge

//導入fillMaxWidth
import androidx.compose.foundation.layout.fillMaxWidth

@Composable
fun AchievementsSection(
    achievements: List<Achievement>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth() // 這裡正確使用fillMaxWidth
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "目前排名：Top 40%",
                style = MaterialTheme.typography.titleMedium
            )
            TextButton(onClick = { /* 查看成就 */ }) {
                Text("查看成就 >")
            }
        }

        Text(
            text = "本賽季日期：2024/09/01-2024/10/31",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            achievements.forEach { achievement ->
                AchievementCircle(achievement)
            }
        }
    }
}

@Composable
fun AchievementCircle(achievement: Achievement) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(80.dp)
            .padding(4.dp)
    ) {
        CircularProgressIndicator(
            progress = achievement.progress,
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF4CAF50),
            strokeWidth = 4.dp
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Top",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "${(achievement.progress * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun BadgesSection(
    badges: List<Badge>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth() // 正確使用fillMaxWidth
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "我的微章",
                style = MaterialTheme.typography.titleMedium
            )
            TextButton(onClick = { /* 查看全部 */ }) {
                Text("查看全部 >")
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(badges) { badge ->
                BadgeItem(badge)
            }
        }
    }
}

@Composable
fun BadgeItem(badge: Badge) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(60.dp)
                .background(
                    color = if (badge.isUnlocked)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = badge.icon,
                contentDescription = badge.name,
                tint = if (badge.isUnlocked)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }

        LinearProgressIndicator(
            progress = badge.progress,
            modifier = Modifier
                .width(50.dp)
                .padding(vertical = 4.dp)
        )

        Text(
            text = badge.name,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// Example usage
@Preview
@Composable
fun AchievementScreenPreview() {
    val achievements = listOf(
        Achievement("登入&發票", 0.4f),
        Achievement("任務參與", 1f),
        Achievement("活躍指數", 1f)
    )

    val badges = listOf(
        Badge("noodle", "初次見麵", Icons.Outlined.Restaurant, true, 0.3f),
        Badge("vegetables", "乾巴巴菜蟲", Icons.Outlined.Info, false, 0f),
        Badge("egg", "散蛋", Icons.Outlined.Egg, true, 0.7f),
        Badge("meat", "肉食主義者", Icons.Outlined.LocalDining, true, 1f)
    )

    MaterialTheme {
        Column {
            AchievementsSection(achievements)
            Divider()
            BadgesSection(badges)
        }
    }
}