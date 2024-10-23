package com.example.billapp.firebase

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.billapp.data.models.AchievementDatabase
import com.example.billapp.data.models.AchievementEntity
import com.example.billapp.data.models.BadgeEntity
import kotlinx.coroutines.flow.Flow

class AchievementRepository(private val database: AchievementDatabase) {
    private val achievementDao = database.achievementDao()
    private val badgeDao = database.badgeDao()

    // Achievement related functions
    fun getAllAchievements(): Flow<List<AchievementEntity>> =
        achievementDao.getAllAchievements()

    suspend fun updateAchievementProgress(id: String, progress: Float) {
        achievementDao.updateProgress(id, progress)
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
                    id = "login_achievement",
                    title = "登入&發票",
                    description = "累積登入次數和發票數量",
                    currentProgress = 0f,
                    maxProgress = 100f,
                    color = Color.Blue.toArgb()
                ),
                // Add more default achievements...
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
                    id = "noodle_badge",
                    name = "初次見麵",
                    description = "第一次購買泡麵",
                    iconName = "noodle_icon",
                    currentProgress = 0f,
                    maxProgress = 1f
                ),
                // Add more default badges...
            )
            defaultBadges.forEach { badgeDao.insertBadge(it) }
        }
    }
}
