package com.example.billapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val currentProgress: Float,
    val maxProgress: Float,
    val color: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)
