package com.example.billapp.dept_relation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.billapp.models.DebtRelation
import com.example.billapp.viewModel.MainViewModel
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeptRelationDetailItem(
    viewModel: MainViewModel,
    debtRelation: DebtRelation,
    groupId: String,
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var showRemindConfirmation by remember { mutableStateOf(false) }
    var lastRemindTime by remember { mutableStateOf(debtRelation.lastRemindTimestamp) }
    val context = LocalContext.current

    var fromName by remember { mutableStateOf("") }
    var toName by remember { mutableStateOf("") }
    val canRemind = lastRemindTime == null || (System.currentTimeMillis() - lastRemindTime!!.toDate().time) > 86400000 // 24 hours in ms

    LaunchedEffect(debtRelation.from, debtRelation.to) {
        fromName = viewModel.getUserName(debtRelation.from)
        toName = viewModel.getUserName(debtRelation.to)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))  // 浅绿色背景
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "交易名称: ${debtRelation.name}", style = MaterialTheme.typography.titleMedium)
                Text(text = "from: $fromName -> to: $toName", style = MaterialTheme.typography.bodyMedium)
                Text(text = "$${String.format("%.2f", debtRelation.amount)}", style = MaterialTheme.typography.bodyLarge)
            }
            Row {
                IconButton(onClick = { showBottomSheet = true }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear Debt")
                }
                IconButton(onClick = {
                    if (canRemind) {
                        viewModel.sendDebtReminder(context, debtRelation)
                        lastRemindTime = Timestamp.now()
                        showRemindConfirmation = true
                    } else {
                        // 提示用户一天只能催一次
                    }
                }) {
                    Icon(Icons.Default.Notifications, contentDescription = "催债")
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Clear Debt", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Are you sure you want to clear this debt?")
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { showBottomSheet = false }) {
                        Text("Cancel")
                    }
                    Button(onClick = {
                        viewModel.deleteDeptRelation(groupId = groupId, deptRelationId = debtRelation.id)
                        viewModel.loadGroupDeptRelations(groupId)
                        showBottomSheet = false
                    }) {
                        Text("Confirm")
                    }
                }
            }
        }
    }

    if (showRemindConfirmation) {
        // 可在此顯示催債通知確認的對話框或提示
        AlertDialog(
            onDismissRequest = { showRemindConfirmation = false },
            title = { Text("催債通知已發送") },
            text = { Text("已成功發送催債通知給 $toName。") },
            confirmButton = {
                Button(onClick = { showRemindConfirmation = false }) {
                    Text("OK")
                }
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun DeptRelationDetailItemPreview() {
    val viewModel = MainViewModel()
    val debtRelation = DebtRelation(
        from = "user1",
        to = "user2",
        id = "1",
        name = "test",
        amount = 100.0,
        groupTransactionId = "1"
    )
    DeptRelationDetailItem(viewModel = viewModel, debtRelation = debtRelation, groupId = "1")
}
