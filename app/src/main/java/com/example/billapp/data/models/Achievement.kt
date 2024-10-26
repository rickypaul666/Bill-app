package com.example.billapp.data.models

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Achievement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val currentCount: Int = 0,
    val targetCount: Int = 0,
    val color: Int = Color.Blue.toArgb(),
    val lastUpdated: Timestamp = Timestamp.now()
): Parcelable
