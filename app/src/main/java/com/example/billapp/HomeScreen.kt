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
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material3.IconButton //
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
import com.example.billapp.models.PersonalTransaction
import com.example.billapp.models.User
import com.example.billapp.ui.theme.BoxBackgroundColor
import com.example.billapp.ui.theme.Brown2
import com.example.billapp.ui.theme.MainBackgroundColor
import com.example.billapp.ui.theme.MainCardRedColor
import com.example.billapp.ui.theme.Brown1
import com.example.billapp.ui.theme.Brown2
import com.example.billapp.ui.theme.Red
import com.example.billapp.ui.theme.Green
import com.example.billapp.viewModel.AvatarViewModel
import com.example.billapp.viewModel.MainViewModel
import java.util.Calendar
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import com.example.billapp.ui.theme.Gray
import com.example.billapp.ui.theme.VeryDarkGray
import androidx.compose.material.icons.filled.ArrowBack as ArrowB
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.layout.width
import androidx.compose.ui.draw.shadow
import com.example.billapp.ui.theme.Brown4
import com.example.billapp.ui.theme.HightlightWhiteColor
import com.example.billapp.ui.theme.PieGreenColor
import com.example.billapp.ui.theme.PieRedColor


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
            .fillMaxSize()
            .background(MainBackgroundColor)
    ) {

            val income = filteredIncome
            val expense = filteredExpense
            val total = income + expense
            val balance = income - expense

            val level by remember { mutableStateOf(viewModel.getUserLevel())}
            val trustLevel by remember { mutableStateOf(viewModel.getUserTrustLevel())}
            val budget by remember { mutableStateOf(viewModel.getUserBudget().toString()) } // Add budget state


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {page ->
                when(page){
                    0 -> Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(BoxBackgroundColor)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val pathLeft = Path().apply {
                                moveTo(0f, 0f)
                                lineTo(size.width * 0.5f, 0f)
                                lineTo(size.width * 0.4f, size.height)
                                lineTo(0f, size.height)
                                close()
                            }
                            val pathRight = Path().apply {
                                moveTo(size.width * 0.5f, 0f)
                                lineTo(size.width, 0f)
                                lineTo(size.width, size.height)
                                lineTo(size.width * 0.4f, size.height)
                                close()
                            }
                            drawPath(pathLeft, BoxBackgroundColor)
                            drawPath(pathRight, MainCardRedColor)
                            drawLine(
                                color = Color.Gray,
                                start = Offset(size.width * 0.5f, 0f),
                                end = Offset(size.width * 0.4f, size.height),
                                strokeWidth = 4f
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(150.dp) // Adjust the size as needed
                                        .padding(8.dp)
                                        .align(Alignment.CenterHorizontally)
                                ) {
                                    AsyncImage(
                                        model = coil.request.ImageRequest.Builder(LocalContext.current)
                                            .data(userImage)
                                            .crossfade(true)
                                            .build(),
                                        placeholder = painterResource(R.drawable.ic_user_place_holder),
                                        contentDescription = "User Image",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.clip(CircleShape)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(user!!.name)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    RoundedCornerProgressBar(
                                        TargetProgress = 1f,
                                        text = "信譽點數: $trustLevel/100",
                                        color = Color.Green,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    RoundedCornerProgressBar(
                                        TargetProgress = 0.5f,
                                        text = "社交值: 等級: lv.$level",
                                        color = Color.Blue,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    RoundedCornerProgressBar(
                                        TargetProgress = 0.083f,
                                        text = "預算: ____/$budget",
                                        color = Color.Red,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }
                        }
                    }
                    1 -> Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(BoxBackgroundColor)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val pathLeft = Path().apply {
                                moveTo(0f, 0f)
                                lineTo(size.width * 0.5f, 0f)
                                lineTo(size.width * 0.4f, size.height)
                                lineTo(0f, size.height)
                                close()
                            }
                            val pathRight = Path().apply {
                                moveTo(size.width * 0.5f, 0f)
                                lineTo(size.width, 0f)
                                lineTo(size.width, size.height)
                                lineTo(size.width * 0.4f, size.height)
                                close()
                            }
                            drawPath(pathLeft, BoxBackgroundColor)
                            drawPath(pathRight, MainCardRedColor)
                            drawLine(
                                color = Color.Gray,
                                start = Offset(size.width * 0.5f, 0f),
                                end = Offset(size.width * 0.4f, size.height),
                                strokeWidth = 4f
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.avatar_placeholder_2),
                                        contentDescription = "Character Avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(80.dp) // 設置頭像大小
                                            .clip(CircleShape) // 設置頭像為圓形
                                            .background(Color.Gray) // 頭像背景顏色
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "AMY",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f) // 確保 Box 是正方形
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                PieChart(
                                    income = income,
                                    expense = expense,
                                    balance = balance,
                                    total = total,
                                    modifier = Modifier.size(80.dp) // 調整圓餅圖大小
                                )

                                // 顯示支出、收入和結餘
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(start = 4.dp, top = 4.dp) // 調整位置
                                        .background(Color.White, RoundedCornerShape(4.dp))
                                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                        .padding(4.dp)
                                ) {
                                    Text(
                                        text = "支出: $expense",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(end = 4.dp, top = 4.dp) // 調整位置
                                        .background(Color.White, RoundedCornerShape(4.dp))
                                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                        .padding(4.dp)
                                ) {
                                    Text(
                                        text = "收入: $income",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .offset(x = (-16).dp,y=(-32).dp)
                                        .padding(start = 4.dp, bottom = 4.dp) // 調整位置
                                        .background(Color.White, RoundedCornerShape(4.dp))
                                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                        .padding(4.dp)
                                ) {
                                    Text(
                                        text = "結餘: $balance",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .offset(x = (16).dp,y=(8).dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        IconButton(
                                            onClick = { updateDate(-1) }
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.baseline_navigate_before_24),
                                                contentDescription = "Previous Page"
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .background(Color.White, RoundedCornerShape(4.dp))
                                                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                                .padding(4.dp) // 內部 padding
                                        ) {
                                            Text(
                                                text = "$year/$month",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        IconButton(
                                            onClick = { updateDate(1) }
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.baseline_navigate_next_24),
                                                contentDescription = "Next Page"
                                            )
                                        }
                                    }
                                }
                            }
                        }
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


        Spacer(modifier = Modifier.height(10.dp))

        var currentPage by remember { mutableStateOf(1) }
        val itemsPerPage = 2
        val totalPages = (transactions.size + itemsPerPage - 1) / itemsPerPage

        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(BoxBackgroundColor)
                .border(2.dp, Brown1, RoundedCornerShape(16.dp))
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center // 水平置中
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 0.dp, bottom = 4.dp) // 內部 padding
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
                        .padding(top = 4.dp),
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
                        modifier = Modifier.padding(horizontal = 8.dp)
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

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(BoxBackgroundColor, RoundedCornerShape(16.dp))
                .border(2.dp, Brown1, RoundedCornerShape(16.dp))
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(16.dp)),

            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = BoxBackgroundColor)

        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 0.dp, bottom = 4.dp), // 內部
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material.Text(
                        text = "您的群組",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(groups.take(5).size) { index ->  // 使用 size 獲取前 4 個群組的數量
                        val group = groups[index]  // 根據索引獲取群組
                        GroupItem(group = group, onItemClick = {
                            navController.navigate("groupDetail/${group.id}")
                        })
                    }
                }
            }
        }
    }
}
//        Spacer(modifier = Modifier.height(8.dp))



//// 只用於主頁
//@Composable
//fun GroupItem(group: Group, onItemClick: () -> Unit) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .aspectRatio(1f)
//            .clickable(onClick = onItemClick),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(8.dp),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            AsyncImage(
//                model = group.image,
//                contentDescription = "Group Image",
//                modifier = Modifier
//                    .size(80.dp)
//                    .clip(CircleShape),
//                contentScale = ContentScale.Crop
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = group.name,
//                fontWeight = FontWeight.Bold,
//                textAlign = TextAlign.Center,
//                maxLines = 2,
//                overflow = TextOverflow.Ellipsis
//            )
//        }
//    }
//}


@Composable
fun BusinessCardFront(user: User, userImage: String?, level: Int, trustLevel: Int) {
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val pathLeft = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width * 0.5f, 0f)
                lineTo(size.width * 0.45f, size.height)
                lineTo(0f, size.height)
                close()
            }
            val pathRight = Path().apply {
                moveTo(size.width * 0.5f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height)
                lineTo(size.width * 0.45f, size.height)
                close()
            }
//            drawPath(pathLeft, Brown1)
//            drawPath(pathRight, MainCardRedColor)
            drawPath(pathLeft, MainCardRedColor)
            drawPath(pathRight, Brown1)
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = userImage,
                    contentDescription = "User Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = user.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                ProgressBar("信譽點數", trustLevel, 100, Brown1)
                Spacer(modifier = Modifier.height(8.dp))
                ProgressBar("社交值", level, 100, Brown2)
                Spacer(modifier = Modifier.height(8.dp))
                ProgressBar("血條", 25, 100, Red)
            }
        }
    }
}

@Composable
fun BusinessCardBack(income: Float, expense: Float, balance: Float, total: Float, year: Int, month: Int, onUpdateDate: (Int) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        // 背景
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(Brown1)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onUpdateDate(-1) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month", tint = Color.White)
                }
                Text(
                    text = "$year/$month",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                IconButton(onClick = { onUpdateDate(1) }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next Month", tint = Color.White)
                }
            }


            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                PieChart(
                    income = income,
                    expense = expense,
                    balance = balance,
                    total = total,
                    modifier = Modifier.size(140.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FinancialInfoBox("收入", income, PieGreenColor, Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                FinancialInfoBox("支出", expense, PieRedColor, Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                FinancialInfoBox("結餘", balance, if (balance >= 0) Green else Red, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun ProgressBar(label: String, current: Int, max: Int, color: Color) {
    Column {
        Text(
            text = "$label: $current/$max",
            color = Color.White,
            fontSize = 14.sp
        )
        LinearProgressIndicator(
            progress = current.toFloat() / max,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            backgroundColor = Color.White.copy(alpha = 0.3f)
        )
    }
}

@Composable
fun FinancialInfoBox(label: String, value: Float, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(color.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "%.2f".format(value),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Composable
fun HomeScreenPersonalTransactionList(
    transactions: List<PersonalTransaction>,
    navController: NavController,
    viewModel: MainViewModel,
    currentPage: Int,
    itemsPerPage: Int
) {
    val startIndex = (currentPage - 1) * itemsPerPage
    val endIndex = minOf(startIndex + itemsPerPage, transactions.size)
    val pageTransactions = transactions.subList(startIndex, endIndex)

    Column {
        pageTransactions.forEach { transaction ->
            TransactionItem(transaction, navController, viewModel)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TransactionItem(
    transaction: PersonalTransaction,
    navController: NavController,
    viewModel: MainViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(color = BoxBackgroundColor),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.name,
                    fontWeight = FontWeight.Bold,
                    color = VeryDarkGray
                )
                Text(
                    text = transaction.date?.toDate()?.toString() ?: "",
                    fontSize = 12.sp,
                    color = Gray
                )
            }
            Text(
                text = "${if (transaction.type == "收入") "+" else "-"}${transaction.amount}",
                fontWeight = FontWeight.Bold,
                color = if (transaction.type == "收入") Green else Red
            )
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(onClick = { /* Edit action */ }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Brown1)
            }
            IconButton(onClick = { /* Delete action */ }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Red)
            }
        }
    }
}

//@Composable
//fun GroupList(groups: List<Group>, navController: NavController) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//    ) {
//        Text(
//            text = "您的群組",
//            fontSize = 20.sp,
//            fontWeight = FontWeight.Bold,
//            color = VeryDarkGray,
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//        LazyRow(
//            horizontalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            items(groups.take(4)) { group ->
//                GroupItem(group = group) {
//                    navController.navigate("groupDetail/${group.id}")
//                }
//            }
//        }
//    }
//}

//@Composable
//fun GroupItem(group: Group, onItemClick: () -> Unit) {
//    Card(
//        modifier = Modifier
//            .width(120.dp)
//            .aspectRatio(0.75f)
//            .clickable(onClick = onItemClick)
//            .background(color = BoxBackgroundColor),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        shape = RoundedCornerShape(8.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(8.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            AsyncImage(
//                model = group.image,
//                contentDescription = "Group Image",
//                modifier = Modifier
//                    .size(80.dp)
//                    .clip(CircleShape)
//                    .border(2.dp, Color.White, CircleShape),
//                contentScale = ContentScale.Crop
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = group.name,
//                fontWeight = FontWeight.Bold,
//                fontSize = 14.sp,
//                color = VeryDarkGray,
//                textAlign = TextAlign.Center,
//                maxLines = 2,
//                overflow = TextOverflow.Ellipsis
//            )
//        }
//    }
//}

//2
@Composable
fun GroupItem(group: Group, onItemClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(105.dp)
            .clickable(onClick = onItemClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = group.image,
            contentDescription = "Group Image",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .border(2.dp, Brown1, CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = group.name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}









//@Composable
//fun UserGroupsCard(groups: List<Group>, navController: NavController) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//            Text(
//                text = "Your Groups",
//                style = MaterialTheme.typography.titleLarge,
//                color = MaterialTheme.colorScheme.onSurface
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//            LazyRow(
//                horizontalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                items(groups.take(4)) { group ->
//                    GroupItem(group = group) {
//                        navController.navigate("groupDetail/${group.id}")
//                    }
//                }
//            }
//        }
//    }
//}
//











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






