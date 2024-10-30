package com.example.billapp.Achievement

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billapp.R
import com.example.billapp.data.models.Achievement
import com.example.billapp.data.models.Badge
import com.example.billapp.ui.theme.theme.BoxBackgroundColor
import com.example.billapp.ui.theme.theme.Brown1
import com.example.billapp.ui.theme.theme.Brown2
import com.example.billapp.ui.theme.theme.Brown3
import com.example.billapp.ui.theme.theme.Brown4
import com.example.billapp.ui.theme.theme.MainBackgroundColor
import com.example.billapp.ui.theme.theme.Orange1
import com.example.billapp.ui.theme.theme.Orange3

val GrayBackgroundColor = Color(0xFFF5F5F5) // 淺灰色背景
val Brown7 = Color(0xFF8B4513)
val BadgeBackgroundColor = Color(0xFFE8E8E8) // 徽章底色改為淺灰
val BadgeUnlockedColor = Color(0xFFDCC48D) // 解鎖後的徽章底色改為溫暖的金色

@Composable
fun AchievementsSection(
    achievements: List<Achievement>,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(BoxBackgroundColor, RoundedCornerShape(16.dp))
            .border(2.dp, Brown1, RoundedCornerShape(16.dp))
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = BoxBackgroundColor)
    )  {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp, bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "成就",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                achievements.take(3).forEach { achievement ->
                    AchievementCircleUpdated(achievement)
                }
            }

            TextButton(
                onClick = onViewAllClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("查看成就 >", color = Brown1)
            }
        }
    }
}

@Composable
fun AchievementCircleUpdated(achievement: Achievement) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(80.dp)
        ) {
            // 外圈進度
            CircularProgressIndicator(
                progress = achievement.currentCount.toFloat() / achievement.targetCount.toFloat(),
                modifier = Modifier.fillMaxSize(),
                color = Color(achievement.color),
                strokeWidth = 8.dp
            )

            // 內圈背景
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(GrayBackgroundColor, CircleShape)
                    .border(2.dp, Color(achievement.color), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${((achievement.currentCount.toFloat() / achievement.targetCount.toFloat()) * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = achievement.title,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun BadgesSection(
    badges: List<Badge>,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(BoxBackgroundColor, RoundedCornerShape(16.dp))
            .border(2.dp, Brown1, RoundedCornerShape(16.dp))
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = BoxBackgroundColor)
    )  {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp, bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "徽章",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // 使用 Grid 替代 LazyVerticalGrid
            Column(modifier = Modifier.fillMaxWidth()) {
                for (i in 0..1) { // 2行
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        badges.drop(i * 4).take(4).forEach { badge ->
                            BadgeItem(
                                badge = badge,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // 如果徽章不足4個，用空白填充
                        repeat(4 - minOf(4, badges.drop(i * 4).size)) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            TextButton(
                onClick = onViewAllClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("查看全部 >", color = Brown1)
            }
        }
    }
}

@Composable
fun BadgeItem(
    badge: Badge,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(60.dp)
                .background(
                    color = if (badge.unlocked) BadgeUnlockedColor else BadgeBackgroundColor,
                    shape = CircleShape
                )
                .border(
                    width = 2.dp,
                    color = if (badge.unlocked) Brown1 else Color.Gray,
                    shape = CircleShape
                )
        ) {
            Icon(
                painter = getIconForName(badge.iconName),
                contentDescription = badge.name,
                modifier = Modifier.size(36.dp),
                tint = if (badge.unlocked) Color.Unspecified else Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = badge.name,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = if (badge.unlocked) Color.Black else Color.Gray
        )
    }
}

@Composable
fun getIconForName(name: String): Painter {
    return when (name) {
        "handshake_icon" -> painterResource(id = R.drawable.handshake_icon)
        "piggy_bank_icon" -> painterResource(id = R.drawable.piggy_bank_icon)
        "star_icon" -> painterResource(id = R.drawable.star_icon)
        "shield_icon" -> painterResource(id = R.drawable.shield_icon)
        "sword_icon" -> painterResource(id = R.drawable.sword_icon)
        "accounting_icon" -> painterResource(id = R.drawable.accounting_icon)
        "trust_icon" -> painterResource(id = R.drawable.trust_icon)
        "quick_clear_icon" -> painterResource(id = R.drawable.quick_clear_icon)
        "social_master_icon" -> painterResource(id = R.drawable.social_master_icon)
        "saving_icon" -> painterResource(id = R.drawable.saving_icon)
        "streak_icon" -> painterResource(id = R.drawable.streak_icon)
        else -> painterResource(id= R.drawable.ic_board_place_holder)
    }
}

@Composable
fun DetailedAchievementsScreen(
    achievements: List<Achievement>,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackgroundColor)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = Brown1
                )
            }
            Text(
                "成就列表",
                style = MaterialTheme.typography.titleLarge,
                color = Brown1,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(achievements) { achievement ->
                AchievementListItem(achievement)
            }
        }
    }
}

@Composable
fun AchievementListItem(achievement: Achievement) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, Brown4, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Brown1
                )
                Text(
                    text = "${achievement.currentCount}/${achievement.targetCount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Brown2
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = achievement.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Brown4
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = achievement.currentCount.toFloat() / achievement.targetCount,
                modifier = Modifier.fillMaxWidth(),
                color = Orange1,
                trackColor = Orange3.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun DetailedBadgesScreen(
    badges: List<Badge>,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackgroundColor)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = Brown1
                )
            }
            Text(
                "徽章列表",
                style = MaterialTheme.typography.titleLarge,
                color = Brown1,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(badges) { badge ->
                BadgeListItem(badge)
            }
        }
    }
}

@Composable
fun BadgeListItem(badge: Badge) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, Brown4, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = if (badge.unlocked) Orange3.copy(alpha = 0.2f) else Brown3.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
                    .border(
                        width = 2.dp,
                        color = if (badge.unlocked) Orange1 else Brown2,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    painter = getIconForName(badge.iconName),
                    contentDescription = badge.name,
                    tint = if (badge.unlocked) Color.Unspecified else Brown2,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = badge.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Brown1
                )
                Text(
                    text = badge.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Brown4
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = badge.currentProgress / badge.maxProgress,
                    modifier = Modifier.fillMaxWidth(),
                    color = Orange1,
                    trackColor = Orange3.copy(alpha = 0.3f)
                )
            }
        }
    }
}


