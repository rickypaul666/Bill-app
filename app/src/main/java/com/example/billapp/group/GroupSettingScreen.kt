package com.example.billapp.group

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.billapp.R
import com.example.billapp.data.models.Group
import com.example.billapp.data.models.GroupTransaction
import com.example.billapp.data.models.User
import com.example.billapp.ui.theme.*
import com.example.billapp.viewModel.AvatarViewModel
import com.example.billapp.viewModel.MainViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSettingScreen(
    groupId: String,
    viewModel: MainViewModel,
    avatarViewModel: AvatarViewModel,
    navController: NavController
) {
    val isBottomSheetVisible = remember { mutableStateOf(false) }
    val group by viewModel.getGroup(groupId).collectAsState(initial = null)
    val deptRelations by viewModel.groupIdDebtRelations.collectAsState()
    val currentUser by viewModel.user.collectAsState()
    val totalDebt by viewModel.totalDebtMap.collectAsState()
    val userImage = avatarViewModel.avatarUrl.collectAsState().value
    val user by viewModel.user.collectAsState()

    LaunchedEffect(groupId) {
        viewModel.getGroupDebtRelations(groupId)
        viewModel.calculateTotalDebtForGroup(groupId)
    }

    val groupTotalDebt = totalDebt[groupId] ?: 0.0

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(group?.name ?: "Group Detail", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { isBottomSheetVisible.value = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Manage", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Orange1)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MainBackgroundColor)
                .verticalScroll(rememberScrollState())
        ) {
            UserInfoSection(user, userImage, groupTotalDebt) {
                navController.navigate("deptRelationsScreen/$groupId")
            }

            MemberAvatarsSection(avatarViewModel, group, navController, groupId)

            RecentTransactionsSection(navController, groupId, viewModel)
        }
    }

    ManagementBottomSheet(
        isVisible = isBottomSheetVisible.value,
        onDismiss = { isBottomSheetVisible.value = false },
        groupId = groupId,
        viewModel = viewModel,
        navController = navController
    )
}

@Composable
fun UserInfoSection(user: User?, userImage: String?, groupTotalDebt: Double, onViewDebtRelations: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BoxBackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(userImage)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = user?.name ?: "User Name",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            DebtInfoCard(groupTotalDebt, onViewDebtRelations)
        }
    }
}

@Composable
fun UserAvatar(userImage: String?) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .border(2.dp, Orange1, CircleShape)
    ) {
        if (userImage != null) {
            AsyncImage(
                model = coil.request.ImageRequest.Builder(LocalContext.current)
                    .data(userImage)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_user_place_holder),
                contentDescription = "User Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = painterResource(R.drawable.ic_user_place_holder),
                contentDescription = "User Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun DebtInfoCard(groupTotalDebt: Double, onViewDebtRelations: () -> Unit) {
    Column {
        DebtAmountBox(groupTotalDebt)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onViewDebtRelations,
            colors = ButtonDefaults.buttonColors(containerColor = Orange1),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("查看所有債務關係", color = Color.White)
        }
    }
}

@Composable
fun DebtAmountBox(amount: Double) {
    val (backgroundColor, textColor) = when {
        amount < 0 -> Color(0xFFFFA8A8) to Color.Red
        amount > 0 -> Color(0xFFC6FFD5) to Color(0xFF228B22)
        else -> Color(0xFFFFEEBB) to Orange4
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(
            text = when {
                amount < 0 -> "應付金額"
                amount > 0 -> "應收金額"
                else -> "帳務已結清"
            },
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black,
            modifier = Modifier.align(Alignment.TopStart)
        )
        Text(
            text = when {
                amount != 0.0 -> "NT$ ${kotlin.math.abs(amount)}"
                else -> "NT$ 0"
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

@Composable
fun MemberAvatarsSection(
    avatarViewModel: AvatarViewModel,
    group: Group?,
    navController: NavController,
    groupId: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BoxBackgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "群組成員",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = { navController.navigate("memberListScreen/$groupId") },
                    colors = ButtonDefaults.buttonColors(containerColor = Orange1),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Filled.List, contentDescription = "Member List")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("詳細列表", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(group?.assignedTo ?: emptyList()) { memberId ->
                    MemberAvatar(avatarViewModel, memberId)
                }
                item {
                    AddMemberButton {
                        navController.navigate("Group_Invite/$groupId")
                    }
                }
            }
        }
    }
}

@Composable
fun MemberAvatar(avatarViewModel: AvatarViewModel, memberId: String) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .border(2.dp, Orange1, CircleShape)
    ) {
        var imageUrl by remember { mutableStateOf("") }

        LaunchedEffect(memberId) {
            imageUrl = avatarViewModel.loadAvatar(memberId).toString()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
        ) {
            if (imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = coil.request.ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.ic_user_place_holder),
                    contentDescription = "User Image",
                    error = painterResource(R.drawable.ic_user_place_holder),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.ic_user_place_holder),
                    contentDescription = "User Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun AddMemberButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .border(2.dp, Orange1, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Member",
            tint = Orange1,
            modifier = Modifier.size(24.dp)
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RecentTransactionsSection(
    navController: NavController,
    groupId: String,
    viewModel: MainViewModel
) {
    val transactions by viewModel.groupTransactions.collectAsState()
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(groupId) {
        viewModel.loadGroupTransactions(groupId)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BoxBackgroundColor)
    ) {
        Spacer(modifier = Modifier.width(4.dp))
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "近期交易",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = { navController.navigate("groupTest/$groupId") },
                    colors = ButtonDefaults.buttonColors(containerColor = Orange1),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Transaction")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("新增交易", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                }
            }

            // Reduced spacing here
            Spacer(modifier = Modifier.height(8.dp))

            if (transactions.isNotEmpty()) {
                HorizontalPager(
                    count = transactions.size,
                    state = pagerState,
                    modifier = Modifier.height(150.dp)
                ) { page ->
                    TransactionItem(transactions[page], viewModel)
                }

                // Reduced padding here
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(8.dp),  // Changed from 16.dp to 8.dp
                )
            } else {
                Text(
                    "No transactions yet",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: GroupTransaction, viewModel: MainViewModel) {
    var payerName by remember { mutableStateOf("") }

    LaunchedEffect(transaction.payer) {
        if (transaction.payer.isNotEmpty()) {
            payerName = viewModel.getUserName(transaction.payer[0])
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8D8)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = transaction.name,
                style = MaterialTheme.typography.titleLarge,  // 改大標題大小
                fontWeight = FontWeight.Bold  // 加粗標題
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 付款人和日期放在同一行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "付款人: $payerName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                // 格式化日期顯示
                Text(
                    text = transaction.date?.toDate()?.let {
                        SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(it)
                    } ?: "N/A",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$${transaction.amount}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = transaction.type,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (transaction.type == "收入") Color.Green else Color.Red
                )
            }
        }
    }
}

@Composable
fun ManagementBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    groupId: String,
    viewModel: MainViewModel,
    navController: NavController
) {
    CustomBottomSheet(isVisible = isVisible, onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .border(BorderStroke(1.dp, Color.LightGray), RoundedCornerShape(16.dp))
        ) {
            Text(
                "群組管理",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            Divider()
            BottomSheetButton(
                text = "成員",
                icon = Icons.Default.Add,
                onClick = { navController.navigate("memberListScreen/$groupId") }
            )
            BottomSheetButton(
                text = "群組邀請連結",
                icon = Icons.Default.Add,
                onClick = { navController.navigate("Group_Invite/$groupId") }
            )
            Divider()
            BottomSheetButton(
                text = "刪除群組",
                icon = Icons.Default.Add,
                onClick = {
                    viewModel.deleteGroup(groupId)
                    navController.navigateUp()
                },
                color = Color.Red
            )
        }
    }
}

@Composable
fun BottomSheetButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    color: Color = Color.Black
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text, color = color, style = MaterialTheme.typography.bodyLarge)
        }
    }
}