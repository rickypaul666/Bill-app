package com.example.billapp.data.models

import com.google.firebase.Timestamp

data class DebtReminder(
    val id: String = "",
    val debtRelationId: String = "", // 關聯的債務ID
    val amount: Double = 0.0,
    val creditorId: String = "", // 債權人ID
    val creditorName: String = "", // 債權人名稱
    val createdAt: Timestamp = Timestamp.now(),
    val isRead: Boolean = false
)
