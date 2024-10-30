package com.example.billapp.personal

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

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
            TextButton(onClick = {
                onYearSelected(selectedYear)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("選擇年份", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
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
                            color = if (year == selectedYear) Color.Blue else Color.Black
                        )
                    }
                }
            }
        }
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
            TextButton(onClick = {
                onMonthSelected(selectedYear, selectedMonth)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
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
                                color = if (year == selectedYear) Color.Blue else Color.Black
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
                                color = if (month == selectedMonth) Color.Blue else Color.Black
                            )
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        content = {
            DatePicker(state = datePickerState)
        }
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
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
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
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
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