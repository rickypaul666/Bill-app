package com.example.billapp.personal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.billapp.ui.theme.theme.BottomBackgroundColor

@Composable
fun YearPickerDialog(
    onYearSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    var selectedYear by remember { mutableStateOf(currentYear) }

    // LazyListState for controlling the scroll position
    val yearListState = rememberLazyListState()

    // Scroll to the current year when the composable is first displayed
    LaunchedEffect(Unit) {
        yearListState.scrollToItem(currentYear - 2010)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                onYearSelected(selectedYear)
                onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(50)
            ) {
                Text("OK", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(50)
            ) {
                Text("Cancel", color = Color.Red)
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp) // 增加內邊距
            ) {
                Text("選擇年份", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(
                    state = yearListState,
                    modifier = Modifier
                        .height(200.dp) // 限制高度以便滾動
                        .fillMaxWidth()
                ) {
                    items((2010..currentYear).toList()) { year ->
                        Text(
                            text = year.toString(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedYear = year
                                }
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = if (year == selectedYear) FontWeight.Bold else FontWeight.Normal,
                            color = if (year == selectedYear) Color.Red else Color.White
                        )
                    }
                }
            }
        },
        containerColor = BottomBackgroundColor,
        modifier = Modifier.fillMaxWidth(0.8f) // 限制對話框的最大寬度
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthPickerDialog(
    onMonthSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
    var selectedYear by remember { mutableStateOf(currentYear) }
    var selectedMonth by remember { mutableStateOf(currentMonth) }

    // LazyListState for controlling the scroll position
    val yearListState = rememberLazyListState()
    val monthListState = rememberLazyListState()

    // Scroll to the current year and month when the composable is first displayed
    LaunchedEffect(Unit) {
        yearListState.scrollToItem(currentYear - 2010)
        monthListState.scrollToItem(currentMonth)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onMonthSelected(selectedYear, selectedMonth)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(50)
            ) {
                Text("OK", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(50)
            ) {
                Text("Cancel", color = Color.Red)
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("選擇年份和月份", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    // Year Picker
                    LazyColumn(
                        state = yearListState,
                        modifier = Modifier.weight(1f).height(200.dp) // 限制高度以便滾動
                    ) {
                        items((2010..currentYear).toList()) { year ->
                            Text(
                                text = year.toString(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedYear = year
                                    }
                                    .padding(16.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = if (year == selectedYear) FontWeight.Bold else FontWeight.Normal,
                                color = if (year == selectedYear) Color.Red else Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Month Picker
                    LazyColumn(
                        state = monthListState,
                        modifier = Modifier.weight(1f).height(200.dp) // 限制高度以便滾動
                    ) {
                        items((0..11).toList()) { month ->
                            Text(
                                text = (month + 1).toString(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedMonth = month
                                    }
                                    .padding(16.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = if (month == selectedMonth) FontWeight.Bold else FontWeight.Normal,
                                color = if (month == selectedMonth) Color.Red else Color.White
                            )
                        }
                    }
                }
            }
        },
        containerColor = BottomBackgroundColor,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    // 設置預設日期為 2023 年 1 月 1 日
    val initialDate = Calendar.getInstance().timeInMillis

    // 創建 DatePickerState
    val datePickerState = rememberDatePickerState()

    // 設置初始日期
    LaunchedEffect(Unit) {
        datePickerState.selectedDateMillis = initialDate
    }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(50)
            ) {
                Text("OK", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(50)
            ) {
                Text("Cancel", color = Color.Red)
            }
        },
        content = {
            Box(
                modifier = Modifier
                    .background(BottomBackgroundColor) // 設置底色
            ) {
                DatePicker(state = datePickerState)
            }
        },
        colors = DatePickerDefaults.colors(BottomBackgroundColor)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(50)
            ) {
                Text("OK", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(50)
            ) {
                Text("Cancel", color = Color.Red)
            }
        },
        colors = DatePickerDefaults.colors(BottomBackgroundColor)
    ) {
        DatePicker(state = datePickerState)
    }
}


// DatePicker.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    onDateSelected: (Long, Long) -> Unit,
    onDismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(
                        dateRangePickerState.selectedStartDateMillis ?: 0L,
                        dateRangePickerState.selectedEndDateMillis ?: 0L
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(50)
            ) {
                Text("OK", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(50)
            ) {
                Text("Cancel", color = Color.Red)
            }
        },
        colors = DatePickerDefaults.colors(BottomBackgroundColor)
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = "選擇起訖日期"
                )
            },
            showModeToggle = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp)
        )
    }
}

@Composable
fun RangeDatePickerDialog(
    onDateRangeSelected: (Long, Long) -> Unit,
    onDismiss: () -> Unit
) {
    CustomDatePickerDialog(
        onDateSelected = { start, end ->
            onDateRangeSelected(start, end)
        },
        onDismiss = onDismiss
    )
}