package com.example.billapp.data.models

import androidx.compose.ui.graphics.vector.ImageVector

data class Badge(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val isUnlocked: Boolean = false,
    val progress: Float = 0f
)