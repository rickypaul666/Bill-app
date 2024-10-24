package com.example.billapp.firebase

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.billapp.data.models.AchievementDatabase
import com.example.billapp.data.models.AchievementEntity
import com.example.billapp.data.models.BadgeEntity
import com.example.billapp.ui.theme.Orange1
import com.example.billapp.ui.theme.Orange4
import com.example.billapp.ui.theme.Purple40
import kotlinx.coroutines.flow.Flow

class AchievementRepository(private val database: AchievementDatabase) {
    private val achievementDao = database.achievementDao()
    private val badgeDao = database.badgeDao()

    // Achievement related functions
    fun getAllAchievements(): Flow<List<AchievementEntity>> =
        achievementDao.getAllAchievements()

    suspend fun updateAchievementProgress(id: String, count: Int) {
        achievementDao.updateProgress(id, count)
    }

    // Badge related functions
    fun getAllBadges(): Flow<List<BadgeEntity>> =
        badgeDao.getAllBadges()

    fun getUnlockedBadges(): Flow<List<BadgeEntity>> =
        badgeDao.getUnlockedBadges()

    suspend fun updateBadgeProgress(id: String, progress: Float) {
        badgeDao.updateProgress(id, progress)
    }

    // Initial data setup
    suspend fun initializeAchievementsIfEmpty() {
        val achievement = achievementDao.getAchievement("login_achievement")
        if (achievement == null) {
            // Insert default achievements
            val defaultAchievements = listOf(
                AchievementEntity(
                    id = "record_master",
                    title = "記帳達人",
                    description = "累積記帳 100 次",
                    currentCount = 0,
                    targetCount = 100,
                    color = Color.Blue.toArgb()
                ),
                AchievementEntity(
                    id = "debt_master",
                    title = "分帳達人",
                    description = "累積完成 50 次分帳",
                    currentCount = 0,
                    targetCount = 50,
                    color = Color.Green.toArgb()
                ),
                AchievementEntity(
                    id = "trust_streak",
                    title = "誠信保持者",
                    description = "保持信任度 100% 超過 30 天",
                    currentCount = 0,
                    targetCount = 30,
                    color = Color.Yellow.toArgb()
                )
            )
            defaultAchievements.forEach { achievementDao.insertAchievement(it) }
        }
    }

    suspend fun initializeBadgesIfEmpty() {
        val badge = badgeDao.getBadge("noodle_badge")
        if (badge == null) {
            // Insert default badges
            val defaultBadges = listOf(
                BadgeEntity(
                    id = "first_split_badge",
                    name = "初次分帳",
                    description = "首次與好友完成分帳",
                    iconName = "handshake_icon",
                    currentProgress = 0f,
                    maxProgress = 1f
                ),
                BadgeEntity(
                    id = "savings_badge",
                    name = "節儉大師",
                    description = "連續 30 天支出少於 1000 元",
                    iconName = "piggy_bank_icon",
                    currentProgress = 0f,
                    maxProgress = 30f
                ),
                BadgeEntity(
                    id = "social_star_badge",
                    name = "社交新星",
                    description = "達到社交等級 3",
                    iconName = "star_icon",
                    currentProgress = 0f,
                    maxProgress = 3f
                ),
                BadgeEntity(
                    id = "trust_shield_badge",
                    name = "信用超人",
                    description = "信任度保持 100% 超過 15 天",
                    iconName = "shield_icon",
                    currentProgress = 0f,
                    maxProgress = 15f
                ),
                BadgeEntity(
                    id = "debt_hero_badge",
                    name = "還款俠客",
                    description = "快速還清超過 10 筆債務",
                    iconName = "sword_icon",
                    currentProgress = 0f,
                    maxProgress = 10f
                ),
                BadgeEntity(
                    id = "accounting_badge",
                    name = "記帳達人",
                    description = "累積記帳 100 次",
                    iconName = "accounting_icon",
                    currentProgress = 0f,
                    maxProgress = 100f,
                    isUnlocked = false
                ),
                BadgeEntity(
                    id = "trust_master_badge",
                    name = "人見人愛",
                    description = "信任度保持 100% 超過一個月",
                    iconName = "trust_icon",
                    currentProgress = 0f,
                    maxProgress = 30f,
                    isUnlocked = false
                ),
                BadgeEntity(
                    id = "quick_debt_clear_badge",
                    name = "快速清帳",
                    description = "欠款後一天內還款三次",
                    iconName = "quick_clear_icon",
                    currentProgress = 0f,
                    maxProgress = 3f,
                    isUnlocked = false
                ),
                BadgeEntity(
                    id = "social_master_badge",
                    name = "社交高手",
                    description = "創建 5 個群組並在每個群組完成一筆分帳交易",
                    iconName = "social_master_icon",
                    currentProgress = 0f,
                    maxProgress = 5f,
                    isUnlocked = false
                ),
                BadgeEntity(
                    id = "savings_master_badge",
                    name = "省錢達人",
                    description = "連續記錄 30 天支出並保持結餘為正",
                    iconName = "savings_icon",
                    currentProgress = 0f,
                    maxProgress = 30f,
                    isUnlocked = false
                ),
                BadgeEntity(
                    id = "accounting_streak_badge",
                    name = "全勤記帳王",
                    description = "連續記帳 7 天",
                    iconName = "streak_icon",
                    currentProgress = 0f,
                    maxProgress = 7f,
                    isUnlocked = false
                )
            )
            defaultBadges.forEach { badgeDao.insertBadge(it) }
        }
    }
}
