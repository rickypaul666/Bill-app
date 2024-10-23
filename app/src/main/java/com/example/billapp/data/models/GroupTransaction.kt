package com.example.billapp.data.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupTransaction(
    val id: String = "",                      // 交易ID
    val name: String = "",
    val payer: List<String> = emptyList(),   // 付錢的人 (userId 列表)
    val divider: List<String> = emptyList(),  // 分錢的人 (userId 列表)
    val shareMethod: String = "",             // 分錢方法(均分、比例、份數、自訂)
    val type: String = "",                    // 交易類型 (支出, 收入)
    val amount: Double = 0.0,                 // 交易金額
    val date: Timestamp? = null,              // 交易日期
    val createdAt: Timestamp? = null,         // 記錄創建的時間戳
    val updatedAt: Timestamp? = null           // 記錄更新的時間戳
) : Parcelable
