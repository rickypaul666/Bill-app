package com.example.billapp.personal

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.example.billapp.models.PersonalTransaction
import com.example.billapp.viewModel.MainViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.billapp.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PersonalTransactionItem(
    transaction: PersonalTransaction,
    onItemClick: () -> Unit,
    onDelete: () -> Unit
) {
    // Format the timestamp to a readable date format
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = transaction.date?.toDate() // Convert Firebase Timestamp to Java Date
    val formattedDate = date?.let { dateFormat.format(it) } ?: "Unknown Date"

    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToStart) {
                showDialog = true
            }
            false
        }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "確認刪除") },
            text = { Text(text = "你確定要刪除此交易紀錄嗎？") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDialog = false
                    }
                ) {
                    Text("確定")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        // 重置 dismissState 以確保交易項目恢復到正常狀態
                        coroutineScope.launch {
                            dismissState.reset()
                        }
                    }
                ) {
                    Text("取消")
                }
            }
        )
    }else{
        coroutineScope.launch {
            dismissState.reset()
        }
    }

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            val progress = dismissState.progress.fraction
            val color = if (dismissState.dismissDirection == DismissDirection.EndToStart) {
                Color.Red.copy(alpha = progress)
            } else {
                Color.Transparent
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        },
        dismissContent = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable(onClick = onItemClick),
                elevation = 4.dp
            ) {
                Box(
                    modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFBBB0A2))  // 外层Box设置padding区域颜色
                    .padding(16.dp)  // 内边距
                 ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .background(Color(0xFFBBB0A2))
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formattedDate
                        )
                        Column {
                            Text(text = transaction.name)
                        }
                        Text(
                            text = "${if (transaction.type == "收入") "+" else "-"}${transaction.amount}",
                            color = if (transaction.type == "收入") Color.Green else Color.Red
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun PersonalTransactionList(
    transactions: List<PersonalTransaction>,
    navController: NavController,
    viewModel: MainViewModel
) {
    LazyColumn {
        items(transactions) { transaction ->
            PersonalTransactionItem(
                transaction = transaction,
                onItemClick = {
                    navController.navigate("editTransaction/${transaction.transactionId}")
                },
                onDelete = {
                    viewModel.deleteTransaction(transaction.transactionId, transaction.type, transaction.amount)
                }
            )
        }
    }
}

//@Composable
//fun PersonalTransactionScreen(
//    navController: NavController,
//    viewModel: MainViewModel,
//    userId: String
//) {
//
//    val transactions by viewModel.userTransactions.collectAsState()
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        Text(
//            text = "Personal Transactions",
//            modifier = Modifier.padding(16.dp)
//        )
//
//        PersonalTransactionList(
//            transactions = transactions,
//            navController = navController,
//            viewModel = viewModel
//        )
//    }
//}