package com.example.billapp.data.models

data class Achievement(
    val title: String,
    val currentCount: Int,
    val maxCount: Int,
    val color: androidx.compose.ui.graphics.Color
)
