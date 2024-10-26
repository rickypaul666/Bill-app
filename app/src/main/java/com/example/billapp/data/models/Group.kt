package com.example.billapp.data.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Group(
    val id: String = "",
    val name: String = "",
    val image: String = "",
    val createdBy: String = "",
    var assignedTo: MutableList<String> = mutableListOf(),
    val transactions: MutableList<GroupTransaction> = mutableListOf(),
    val closedTransactions: MutableList<GroupTransaction> = mutableListOf(),
    @PropertyName("debtRelations")
    val debtRelations: MutableList<DebtRelation> = mutableListOf(),
    val createdTime: Timestamp? = null,
    val imageId: Int = 0
) : Parcelable
