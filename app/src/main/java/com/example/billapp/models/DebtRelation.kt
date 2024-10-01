package com.example.billapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

@Parcelize
data class DebtRelation(
    val id: String = "",
    val name: String = "",
    var groupTransactionId: String = "",
    @PropertyName("from")
    val from: String = "",
    @PropertyName("to")
    val to: String = "",
    @PropertyName("amount")
    val amount: Double = 0.0,
    @PropertyName("lastRemindTimestamp")
    val lastRemindTimestamp: Timestamp? = null,
    val lastPenaltyDate: Timestamp? = null
) : Parcelable
