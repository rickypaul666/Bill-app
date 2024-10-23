package com.example.billapp.viewModel

import android.app.Application
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.billapp.data.models.Achievement
import com.example.billapp.data.models.AchievementDatabase
import com.example.billapp.data.models.Badge
import com.example.billapp.firebase.AchievementRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AchievementViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AchievementRepository
    val achievements: StateFlow<List<Achievement>>
    val badges: StateFlow<List<Badge>>

    init {
        val database = AchievementDatabase.getDatabase(application)
        repository = AchievementRepository(database)

        // Initialize default data
        viewModelScope.launch {
            repository.initializeAchievementsIfEmpty()
            repository.initializeBadgesIfEmpty()
        }

        // Transform database entities to UI models
        achievements = repository.getAllAchievements()
            .map { entities ->
                entities.map { entity ->
                    Achievement(
                        title = entity.title,
                        progress = entity.currentProgress / entity.maxProgress,
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        badges = repository.getAllBadges()
            .map { entities ->
                entities.map { entity ->
                    Badge(
                        id = entity.id,
                        name = entity.name,
                        icon = getIconForName(entity.iconName),
                        isUnlocked = entity.isUnlocked,
                        progress = entity.currentProgress / entity.maxProgress
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun updateAchievementProgress(id: String, progress: Float) {
        viewModelScope.launch {
            repository.updateAchievementProgress(id, progress)
        }
    }

    fun updateBadgeProgress(id: String, progress: Float) {
        viewModelScope.launch {
            repository.updateBadgeProgress(id, progress)
        }
    }

    private fun getIconForName(iconName: String): ImageVector {
        // Map icon names to ImageVector resources
        return when (iconName) {
            "noodle_icon" -> Icons.Outlined.Restaurant
            "vegetable_icon" -> Icons.Outlined.ThumbUp
            // Add more icon mappings...
            else -> Icons.Outlined.Star
        }
    }

    fun initializeDefaultAchievements() {
        viewModelScope.launch {
            repository.initializeAchievementsIfEmpty()
            repository.initializeBadgesIfEmpty()
        }
    }
}