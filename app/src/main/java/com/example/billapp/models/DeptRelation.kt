package com.example.billapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.security.Timestamp

@Parcelize
data class DeptRelation(
    val id: String = "",
    val name: String = "",
    val groupTransactionId: String = "",
    val from: String = "",
    val to: String = "",
    val amount: Double = 0.0,
    val lastRemindTimestamp: Timestamp
) : Parcelable
