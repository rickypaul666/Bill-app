package com.example.billapp.dept_relation

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.billapp.data.models.DebtRelation
import com.example.billapp.ui.theme.Orange4
import com.example.billapp.ui.theme.PrimaryFontColor
import com.example.billapp.viewModel.MainViewModel
import com.google.firebase.Timestamp

object PaymentMethods {
    data class PaymentOption(
        val id: String,
        val icon: ImageVector,
        val label: String,
        val color: Color,
        val appUri: String? = null,
        val webUri: String? = null
    )

    val availablePayments = listOf(
        PaymentOption(
            id = "linepay",
            icon = Icons.Default.Payment,
            label = "Line Pay",
            color = Color(0xFF00B900),
            appUri = "linepay://",
            webUri = "https://pay.line.me/portal/tw/main"
        ),
//        PaymentOption(
//            id = "creditcard",
//            icon = Icons.Default.CreditCard,
//            label = "信用卡",
//            color = Color(0xFF1976D2)
//        ),
//        PaymentOption(
//            id = "bank",
//            icon = Icons.Default.AccountBalance,
//            label = "銀行轉帳",
//            color = Color(0xFF388E3C)
//        ),
        PaymentOption(
            id = "jkopay",
            icon = Icons.Default.Payment,
            label = "街口支付",
            color = Color(0xFFFF6B00),
            appUri = "jkopay://",
            webUri = "https://www.jkopay.com"
        ),
//        PaymentOption(
//            id = "taiwanpay",
//            icon = Icons.Default.Payment,
//            label = "台灣 Pay",
//            color = Color(0xFF00A0E9)
//        ),
        PaymentOption(
            id = "other",
            icon = Icons.Default.Construction,
            label = "未來開放",
            color = Color.Gray
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtRelationDetailItem(
    viewModel: MainViewModel,
    debtRelation: DebtRelation,
    groupId: String,
    modifier: Modifier = Modifier
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var showRemindConfirmation by remember { mutableStateOf(false) }
    var lastRemindTime by remember { mutableStateOf(debtRelation.lastRemindTimestamp) }
    val context = LocalContext.current

    var fromName by remember { mutableStateOf("") }
    var toName by remember { mutableStateOf("") }
    val canRemind = lastRemindTime?.let {
        (System.currentTimeMillis() - it.toDate().time) > 86400000
    } ?: true

    val currentUser = viewModel.user.collectAsState().value
    val userId = remember(currentUser) { currentUser?.id.orEmpty() }

    // Load user names
    LaunchedEffect(debtRelation.from, debtRelation.to) {
        fromName = viewModel.getUserName(debtRelation.from)
        toName = viewModel.getUserName(debtRelation.to)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DebtInfo(
                debtName = debtRelation.name,
                fromName = fromName,
                toName = toName,
                amount = debtRelation.amount
            )

            ActionButtons(
                onClearDebt = { showBottomSheet = true },
                onRemindDebt = {
                    if (canRemind) {
                        viewModel.sendDebtReminder(context, debtRelation)
                        lastRemindTime = Timestamp.now()
                        showRemindConfirmation = true
                    }
                },
                canRemind = canRemind
            )
        }
    }

    // Payment Bottom Sheet
    if (showBottomSheet) {
        PaymentBottomSheet(
            onDismiss = { showBottomSheet = false },
            onConfirm = {
                viewModel.deleteDebtRelation(
                    groupId = groupId,
                    debtRelationId = debtRelation.id
                )
                viewModel.loadGroupDebtRelations(groupId)
                viewModel.updateUserExperience(userId, 5)
                showBottomSheet = false
            },
            context = context
        )
    }

    // Reminder Confirmation Dialog
    if (showRemindConfirmation) {
        ReminderConfirmationDialog(
            toName = toName,
            onDismiss = { showRemindConfirmation = false }
        )
    }
}

@Composable
private fun DebtInfo(
    debtName: String,
    fromName: String,
    toName: String,
    amount: Double,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(8.dp) // 增加 padding
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = debtName,
                style = MaterialTheme.typography.headlineSmall,
                color = PrimaryFontColor
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "$${String.format("%.2f", amount)}",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = PrimaryFontColor
            )
        }

        Spacer(modifier = Modifier.height(4.dp)) // 增加間距

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$fromName",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Direction",
                tint = PrimaryFontColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$toName",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ActionButtons(
    onClearDebt: () -> Unit,
    onRemindDebt: () -> Unit,
    canRemind: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(onClick = onClearDebt) {
            Icon(
                Icons.Default.CreditCard,
                contentDescription = "Clear Debt",
                tint = Color(0xFF6650a4)
            )
        }

        IconButton(
            onClick = onRemindDebt,
            enabled = canRemind
        ) {
            Icon(
                Icons.Default.Notifications,
                contentDescription = "提醒付款",
                tint = if (canRemind)
                    Orange4
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        }
    }
}

@Composable
private fun ReminderConfirmationDialog(
    toName: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("催債通知已發送") },
        text = { Text("已成功發送催債通知給 $toName") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("確定")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    context: Context,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        windowInsets = WindowInsets(0),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp)
                .navigationBarsPadding()
        ) {
            // Header
            Text(
                "選擇付款方式",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                "請選擇您想要使用的支付方式來結清債務",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Payment Grid - 減少高度並移除固定高度限制
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.wrapContentHeight() // 改用 wrapContentHeight
            ) {
                items(PaymentMethods.availablePayments.size) { index ->
                    val payment = PaymentMethods.availablePayments[index]
                    PaymentMethod(
                        icon = payment.icon,
                        label = payment.label,
                        color = payment.color
                    ) {
                        payment.appUri?.let { appUri ->
                            payment.webUri?.let { webUri ->
                                launchPaymentApp(context, appUri, webUri)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp)) // 調整間距

            // Confirmation Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF415C)
                    )
                ) {
                    Text("取消")
                }

                Button(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF93F37C)
                    )
                ) {
                    Text("確認付款")
                }
            }
        }
    }
}

@Composable
fun PaymentMethod(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(32.dp),
            tint = color
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private fun launchPaymentApp(
    context: Context,
    appUri: String,
    webUri: String
) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(appUri))
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUri))
        context.startActivity(webIntent)
    }
}

@Preview(showBackground = true)
@Composable
fun DebtRelationDetailItemPreview() {
    val viewModel = MainViewModel()
    val debtRelation = DebtRelation(
        from = "user1",
        to = "user2",
        id = "1",
        name = "test",
        amount = 100.0,
        groupTransactionId = "1"
    )
    DebtRelationDetailItem(viewModel = viewModel, debtRelation = debtRelation, groupId = "1")
}
