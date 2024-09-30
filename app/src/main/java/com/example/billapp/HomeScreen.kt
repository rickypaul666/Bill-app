package com.example.billapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.billapp.models.Group
import com.example.billapp.ui.theme.BoxBackgroundColor
import com.example.billapp.ui.theme.MainBackgroundColor
import com.example.billapp.ui.theme.MainCardRedColor
import com.example.billapp.viewModel.AvatarViewModel
import com.example.billapp.viewModel.MainViewModel
import java.util.Calendar


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    onOpenDrawer: () -> Unit,
    viewModel: MainViewModel,
    avatarViewModel: AvatarViewModel
) {
    val user = viewModel.user.collectAsState().value
    val userName = viewModel.getCurrentUserName()

    val userImage = avatarViewModel.avatarUrl.collectAsState().value
    var showDialog by remember { mutableStateOf(false) }

    val groups by viewModel.userGroups.collectAsState()
    // 获取最近两笔交易记录
    val transactions by viewModel.userTransactions.collectAsState()
    var filteredRecords by remember { mutableStateOf(transactions) }

    var selectedChart by remember { mutableStateOf("結餘") }

    fun filtered(){
        val filtered = transactions.filter {
            it.updatedAt != null

        }
            .sortedByDescending { it.updatedAt }
            .take(10)

        filteredRecords = filtered.filter { transaction ->
            when (selectedChart) {
                "支出" -> transaction.type == "支出"
                "收入" -> transaction.type == "收入"
                "結餘" -> true
                else -> true
            }
        }

    }

    LaunchedEffect(user) {
        user?.let {
            viewModel.loadUserTransactions()
            filtered()
        }
    }
    LaunchedEffect(transactions) {
        filtered()
    }


    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    var year by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var month by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH) + 1) }
    var day by remember { mutableStateOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) }

    var dateType by remember { mutableStateOf("月") }
    var filteredIncome by remember { mutableStateOf(0f) }
    var filteredExpense by remember { mutableStateOf(0f) }
    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }

    // 根據選中的類型過濾記錄
    fun filterRecords() {
        val filtered = transactions.filter { transaction ->
            val calendar =
                Calendar.getInstance().apply { timeInMillis = transaction.date!!.toDate().time }
            when (dateType) {
                "年" -> calendar.get(Calendar.YEAR) == year
                "月" -> calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) + 1 == month
                "日" -> calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) + 1 == month && calendar.get(
                    Calendar.DAY_OF_MONTH
                ) == day

                "自訂" -> startDate?.let { calendar.timeInMillis >= it } == true && endDate?.let { calendar.timeInMillis <= it } == true

                else -> true
            }
        }
        filteredRecords = filtered.filter { transaction ->
            when (selectedChart) {
                "支出" -> transaction.type == "支出"
                "收入" -> transaction.type == "收入"
                "結餘" -> true
                else -> true
            }
        }
        filteredIncome = filtered.filter { it.type == "收入" }.sumOf { it.amount }.toFloat()
        filteredExpense = filtered.filter { it.type == "支出" }.sumOf { it.amount }.toFloat()
    }

    LaunchedEffect(user) {
        user?.let {
            viewModel.loadUserTransactions()
            filterRecords()
        }
    }
    LaunchedEffect(transactions) {
        filterRecords()
    }

    // 更新日期
    fun updateDate(increment: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day)
        when (dateType) {
            "年" -> calendar.add(Calendar.YEAR, increment)
            "月" -> calendar.add(Calendar.MONTH, increment)
            "日" -> calendar.add(Calendar.DAY_OF_MONTH, increment)
        }
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH) + 1
        day = calendar.get(Calendar.DAY_OF_MONTH)

        // 更新數據
        filterRecords()  // 過濾記錄
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MainBackgroundColor)
    ) {

        val income = filteredIncome
        val expense = filteredExpense
        val total = income + expense
        val balance = income - expense

        val level = viewModel.getUserLevel()
        val trustLevel = viewModel.getUserTrustLevel()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(16.dp)
                .background(BoxBackgroundColor, RoundedCornerShape(16.dp))
                .border(6.dp, Brown1, RoundedCornerShape(16.dp))
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(16.dp))
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when(page) {
                    0 -> BusinessCardFront(user!!, userImage, level, trustLevel)
                    1 -> BusinessCardBack(income, expense, balance, total, year, month, onUpdateDate = { updateDate(it) })
                }
            }
        }

        // Pager indicator
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Brown1 else Brown2
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        var currentPage by remember { mutableStateOf(1) }
        val itemsPerPage = 2
        val totalPages = (transactions.size + itemsPerPage - 1) / itemsPerPage

        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .background(BoxBackgroundColor)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center // 水平置中
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White)
                            .border(1.dp, Color.Gray)
                            .padding(8.dp) // 內部 padding
                    ) {
                        androidx.compose.material.Text(
                            text = "近期交易紀錄",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                HomeScreenPersonalTransactionList(
                    transactions = transactions,
                    navController = navController,
                    viewModel = viewModel,
                    currentPage = currentPage,
                    itemsPerPage = itemsPerPage
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material.IconButton(
                        onClick = {
                            if (currentPage > 1) {
                                currentPage--
                            }
                        },
                        enabled = currentPage > 1
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "Previous Page"
                        )
                    }

                    androidx.compose.material.Text(
                        text = "$currentPage/$totalPages",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    androidx.compose.material.IconButton(
                        onClick = {
                            if (currentPage < totalPages) {
                                currentPage++
                            }
                        },
                        enabled = currentPage < totalPages
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                            contentDescription = "Next Page"
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Box (
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .background(BoxBackgroundColor)
                .padding(8.dp)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center // 水平置中
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White)
                            .border(1.dp, Color.Gray)
                            .padding(8.dp) // 內部 padding
                    ) {
                        Text(
                            text = "您的群組",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 顯示時間最近的 4 個 Group，並利用水平滑動顯示
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.height(320.dp)  // 調整高度
                ) {
                    items(groups.take(4).size) { index ->  // 使用 size 獲取前 4 個群組的數量
                        val group = groups[index]  // 根據索引獲取群組
                        GroupItem(group = group, onItemClick = {
                            navController.navigate("groupDetail/${group.id}")
                        })
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

// 只用於主頁
@Composable
fun GroupItem(group: Group, onItemClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onItemClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = group.image,
                contentDescription = "Group Image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = group.name,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
@Composable
fun EmptyGroupSlot() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // 空的內容，只顯示背景色
    }
}


@Preview(showBackground = true)
@Composable
fun GroupItemPreview(){
    val group = Group(
        id = "1",
        name = "Group 1",
        image = "https://example.com/image1.jpg",
        createdBy = "User 1",
    )
    GroupItem(group = group, onItemClick = {})
}




