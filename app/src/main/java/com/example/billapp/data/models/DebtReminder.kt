package com.example.billapp.data.models

import com.google.firebase.Timestamp
import java.util.UUID

data class DebtReminder(
    val id: String = UUID.randomUUID().toString(),
    val debtRelationId: String = "", // 關聯的債務ID
    val amount: Double = 0.0,
    val creditorId: String = "", // 債權人ID
    val creditorName: String = "", // 債權人名稱
    val createdAt: Timestamp = Timestamp.now(),
    val isRead: Boolean = false
)
