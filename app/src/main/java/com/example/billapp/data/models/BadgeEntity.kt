package com.example.billapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "badges")
data class BadgeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val currentProgress: Float,
    val maxProgress: Float,
    val isUnlocked: Boolean = false,
    val unlockedDate: Long? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)
