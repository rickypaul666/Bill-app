package com.example.billapp

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed


import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billapp.ui.theme.BoxBackgroundColor
import com.example.billapp.ui.theme.DarkerGray
import com.example.billapp.ui.theme.PieGreenColor
import com.example.billapp.ui.theme.PieRedColor
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState


@Composable
fun PieChart(income: Float, expense: Float, balance: Float, total: Float, modifier: Modifier = Modifier) {
    val incomeAngle = (income / total) * 360f
    val expenseAngle = (expense / total) * 360f
    var noneAngle by remember { mutableStateOf(360f) }

    if(income == 0f){
        if(expense == 0f){
            noneAngle = 360f
        }else{
            noneAngle = 0f
        }
    }else{
        noneAngle = 0f
    }

    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Canvas(modifier = Modifier.size(80.dp)) { // 調整圓餅圖大小
            withTransform({
                rotate(270f)
            }) {
                drawArc(
                    color = PieGreenColor,
                    startAngle = 0f,
                    sweepAngle = incomeAngle,
                    useCenter = true, // 使用中心點
                    style = Fill // 改為實心
                )
                drawArc(
                    color = PieRedColor,
                    startAngle = incomeAngle,
                    sweepAngle = expenseAngle,
                    useCenter = true, // 使用中心點
                    style = Fill // 改為實心
                )
                drawArc(
                    color = DarkerGray,
                    startAngle = incomeAngle,
                    sweepAngle = noneAngle,
                    useCenter = true, // 使用中心點
                    style = Fill // 改為實心
                )
            }
        }

    }
}

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalFoundationApi::class, ExperimentalPagerApi::class)
@Composable
fun PieChartWithCategory(income: Float, expense: Float, balance: Float, total: Float, selectedCategory: String, categoryValues: List<Float>){
    if(selectedCategory == "balance"){
        val incomeAngle = (income / total) * 360f
        val expenseAngle = (expense / total) * 360f
        var noneAngle by remember { mutableStateOf(360f) }
        if(balance != 0f){
            noneAngle = 0f
        }else{
            noneAngle = 360f
        }
        Box(contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp) // Add padding to ensure the content has some space
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "收入: ${String.format("%.0f", income)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp),
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = "支出: ${String.format("%.0f", expense)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp),
                        textAlign = TextAlign.End
                    )
                }
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Canvas(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center)
                    ) {
                        withTransform({
                            rotate(270f)
                        }) {
                            drawArc(
                                color = PieGreenColor,
                                startAngle = 0f,
                                sweepAngle = incomeAngle,
                                useCenter = false,
                                style = Stroke(width = 10.dp.toPx())
                            )
                            drawArc(
                                color = PieRedColor,
                                startAngle = incomeAngle,
                                sweepAngle = expenseAngle,
                                useCenter = false,
                                style = Stroke(width = 10.dp.toPx())
                            )
                            drawArc(
                                color = Color.LightGray,
                                startAngle = incomeAngle + expenseAngle,
                                sweepAngle = noneAngle,
                                useCenter = false,
                                style = Stroke(width = 10.dp.toPx())
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(200.dp), // Ensure the column takes the full size of the box
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.capybara_check_bill), // Replace with your image resource ID
                            contentDescription = "Center Image",
                            modifier = Modifier
                                .size(80.dp) // Adjust the size as needed
                        )
                        Text(
                            text = "結餘: ${String.format("%.0f", balance)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp) // Add padding to separate the text from the image
                        )
                    }
                }

            }
        }
    }else if(selectedCategory == "income"){
        val incomeAngle = income *  360f
        var noneAngle by remember { mutableStateOf(360f) }
        if(income!=0f){
            noneAngle = 0f
        }else{
            noneAngle = 360f
        }
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {

                Canvas(modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.Center)
                ) {
                    withTransform({
                        rotate(270f)
                    }) {
                        drawArc(
                            color = PieGreenColor,
                            startAngle = 0f,
                            sweepAngle = incomeAngle,
                            useCenter = false,
                            style = Stroke(width = 10.dp.toPx())
                        )
                        drawArc(
                            color = Color.LightGray,
                            startAngle = incomeAngle,
                            sweepAngle = noneAngle,
                            useCenter = false,
                            style = Stroke(width = 10.dp.toPx())
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(200.dp), // Ensure the column takes the full size of the box
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.capybara_get_salary), // Replace with your image resource ID
                        contentDescription = "Center Image",
                        modifier = Modifier
                            .size(80.dp) // Adjust the size as needed
                    )
                    Text(
                        text = "結餘: ${String.format("%.0f", income)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp) // Add padding to separate the text from the image
                    )
                }
            }
        }
    }else if(selectedCategory == "expanse-2"){
        val expenseAngle = expense * 360f
        var noneAngle by remember { mutableStateOf(360f) }
        if(expense!=0f){
            noneAngle = 0f
        }else{
            noneAngle = 360f
        }
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Canvas(modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.Center)
                ) {
                    withTransform({
                        rotate(270f)
                    }) {
                        drawArc(
                            color = Color.Red,
                            startAngle = 0f,
                            sweepAngle = expenseAngle,
                            useCenter = false,
                            style = Stroke(width = 20.dp.toPx())
                        )
                        drawArc(
                            color = Color.LightGray,
                            startAngle = expenseAngle,
                            sweepAngle = noneAngle,
                            useCenter = false,
                            style = Stroke(width = 20.dp.toPx())
                        )
                    }
                }
                Text(
                    text = "支出: $expense",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }else if (selectedCategory == "expanse") {
        val categoryNames = listOf(
            "shopping",
            "entertainment",
            "transportation",
            "education",
            "living",
            "medical",
            "investment",
            "food",
            "travel",
            "other"
        )

        val categoryColorMap: List<Color> = listOf(
            Color(0xFFDC5019),
            Color(0xFF64D600),
            Color(0xFF00C9D8),
            Color(0xFF513889),
            Color(0xFF2D3B5C),
            Color(0xFF1B3D33),
            Color(0xFF448948),
            Color(0xFF89682A),
            Color(0xFF9E9D3E),
            Color(0xFFDB789E)
        )

        val angles = categoryValues.map { (it / expense) * 360f }
        var noneAngle by remember { mutableStateOf(360f) }
        if(expense != 0f){
            noneAngle = 0f
        }else{
            noneAngle = 360f
        }

        var startAngle = 0f

        val pagerState = rememberPagerState()
        val coroutineScope = rememberCoroutineScope()

        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .height(200.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    count = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                ) { page ->
                    when (page) {
                        0 -> Box(
                            contentAlignment = Alignment.TopCenter,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                ) {
                                    if (expense != 0f) {
                                        // Draw pie chart
                                        Canvas(
                                            modifier = Modifier.size(200.dp)
                                        ) {
                                            withTransform({
                                                rotate(270f)
                                            }) {
                                                var currentStartAngle = startAngle
                                                angles.forEachIndexed { index, sweepAngle ->
                                                    drawArc(
                                                        color = categoryColorMap[index],
                                                        startAngle = currentStartAngle,
                                                        sweepAngle = sweepAngle,
                                                        useCenter = false,
                                                        style = Stroke(width = 10.dp.toPx())
                                                    )
                                                    currentStartAngle += sweepAngle
                                                }
                                            }
                                        }
                                    } else {
                                        // Draw empty chart
                                        Canvas(
                                            modifier = Modifier.size(200.dp)
                                        ) {
                                            withTransform({
                                                rotate(270f)
                                            }) {
                                                drawArc(
                                                    color = Color.LightGray,
                                                    startAngle = 0f,
                                                    sweepAngle = 360f,
                                                    useCenter = false,
                                                    style = Stroke(width = 10.dp.toPx())
                                                )
                                            }
                                        }
                                    }
                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .size(200.dp), // Ensure the column takes the full size of the box
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.capybara_buying_things), // Replace with your image resource ID
                                            contentDescription = "Center Image",
                                            modifier = Modifier
                                                .size(80.dp) // Adjust the size as needed
                                        )
                                        Text(
                                            text = "結餘: ${String.format("%.0f", expense)}",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(top = 8.dp) // Add padding to separate the text from the image
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Box {
                                    // 過濾掉值為 0 的項目，並將結果存入新的列表
                                    val filteredCategoryValues = categoryValues
                                        .mapIndexed { index, value -> index to value }
                                        .filter { it.second > 0 }

                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(3), // 每行顯示三個項目
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        items(filteredCategoryValues.size) { i ->
                                            val (index, value) = filteredCategoryValues[i]

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center,
                                                modifier = Modifier
                                                    .fillMaxWidth()
//                                                    .padding(4.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .background(color = categoryColorMap[index])
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                BasicText(text = categoryNames[index])
                                            }
                                        }
                                    }

                                }
                            }
                        }

                        1 -> Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (expense != 0f) {
                                ProgressBarChart(
                                    categoryNames = categoryNames,
                                    categoryValues = categoryValues,
                                    categoryColorMap = categoryColorMap,
                                    backgroundColor = BoxBackgroundColor
                                )
                            } else {
                                Text(text = "無交易紀錄")
                            }
                        }
                    }
                }

                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(top = 8.dp)
                )
            }
        }

    }
}

@SuppressLint("DefaultLocale")
@Composable
fun ProgressBarChart(
    categoryNames: List<String>,
    categoryValues: List<Float>,
    categoryColorMap: List<Color>,
    backgroundColor: Color
) {
    // Combine the data into a list of triples and sort by value in descending order
    val sortedData = categoryNames.zip(categoryValues).zip(categoryColorMap) { (name, value), color ->
        Triple(name, value, color)
    }.sortedByDescending { it.second }

    val totalValue = categoryValues.sum()

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        items(sortedData) { (name, value, color) ->
            val percentage = (value / totalValue) * 100

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .padding(vertical = 4.dp)
                    .background(color = backgroundColor, shape = RoundedCornerShape(4.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = value / totalValue)
                        .background(color = color, shape = RoundedCornerShape(4.dp))
                )
                Text(
                    text = "$name: ${String.format("%.1f", percentage)}%",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PieChartPreview() {
    PieChart(income = 100f, expense = 50f, balance = 50f, total = 150f)
}