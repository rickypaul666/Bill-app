package com.example.billapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.firebase.Timestamp

@Parcelize
data class DebtRelation(
    val id: String = "",
    val name: String = "",
    var groupTransactionId: String = "",
    val from: String = "",
    val to: String = "",
    val amount: Double = 0.0,
    val lastRemindTimestamp: Timestamp? = null
) : Parcelable
