package com.example.billapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val currentCount: Int,  // 用於紀錄已完成的次數
    val targetCount: Int,   // 目標次數
    val color: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)
