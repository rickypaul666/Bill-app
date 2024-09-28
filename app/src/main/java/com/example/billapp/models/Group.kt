package com.example.billapp.models

import android.os.Parcelable
import com.google.firebase.Timestamp
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
    val deptRelations: MutableList<DeptRelation> = mutableListOf(),
    val createdTime: Timestamp? = null,
    val imageId: Int = 0
) : Parcelable
