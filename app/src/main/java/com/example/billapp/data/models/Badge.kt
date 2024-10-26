package com.example.billapp.data.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Badge(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val iconName: String = "",
    val currentProgress: Float = 0f,
    val maxProgress: Float = 1f,
    val unlocked: Boolean = false,
    val unlockedDate: Timestamp? = null,
    val lastUpdated: Timestamp = Timestamp.now()
): Parcelable