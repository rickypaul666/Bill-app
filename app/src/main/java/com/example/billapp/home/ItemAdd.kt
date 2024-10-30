package com.example.billapp.home

import java.util.Stack
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import com.example.billapp.data.models.TransactionCategory
import com.example.billapp.viewModel.MainViewModel
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import com.example.billapp.R
import com.example.billapp.group.SeparateBottomSheetContent
import com.example.billapp.group.CustomBottomSheet
import com.example.billapp.ui.theme.theme.BoxBackgroundColor
import com.example.billapp.ui.theme.theme.VeryDarkGray
import com.example.billapp.ui.theme.theme.DarkGray
import com.example.billapp.ui.theme.theme.LightGray
import com.example.billapp.ui.theme.theme.Orange1
import com.example.billapp.ui.theme.theme.Brown1
import com.example.billapp.ui.theme.theme.Brown5
import com.example.billapp.ui.theme.theme.ButtonGrayColor
import com.example.billapp.ui.theme.theme.Green
import com.example.billapp.ui.theme.theme.Red
import com.example.billapp.ui.theme.theme.ItemAddMainColor
import com.example.billapp.ui.theme.theme.MainBackgroundColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StylishTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    readOnly: Boolean,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val capybaraBrown = colorResource(id = R.color.colorAccent)

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        readOnly = readOnly,
        textStyle = TextStyle(
            fontSize = 18.sp,
            color = VeryDarkGray,
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(2.dp, Brown1, RoundedCornerShape(8.dp)),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = BoxBackgroundColor,
            focusedIndicatorColor = Brown1,
            focusedLabelColor = Brown5,

            unfocusedIndicatorColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
//        keyboardOptions = keyboardOptions
    )
}


@Composable
fun CustomKeyboard(
    onKeyClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onClearClick: () -> Unit,
    onOkClick: () -> Unit,
    onEqualsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val capybaraBrown = VeryDarkGray
    val capybaraLight = ItemAddMainColor

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        val buttonModifier = Modifier
            .weight(1f)
            .padding(4.dp)

        val buttonColors = ButtonDefaults.buttonColors(
            containerColor = capybaraLight
        )

        // 鍵盤按鈕
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(modifier = buttonModifier .border(2.dp, Brown1, shape = RoundedCornerShape(8.dp)), onClick = { onKeyClick("7") }, colors = buttonColors) {
                Text("7", color = capybaraBrown)
            }
            Button(modifier = buttonModifier .border(2.dp, Brown1, shape = RoundedCornerShape(8.dp)), onClick = { onKeyClick("8") }, colors = buttonColors) {
                Text("8", color = capybaraBrown)
            }
            Button(modifier = buttonModifier .border(2.dp, Brown1, shape = RoundedCornerShape(8.dp)), onClick = { onKeyClick("9") }, colors = buttonColors) {
                Text("9", color = capybaraBrown)
            }
            Button(modifier = buttonModifier .border(2.dp, Brown1, shape = RoundedCornerShape(8.dp)), onClick = { onKeyClick("÷") }, colors = buttonColors) {
                Text("÷", color = capybaraBrown)
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(modifier = buttonModifier .border(2.dp, Brown1, shape = RoundedCornerShape(8.dp)), onClick = { onKeyClick("4") }, colors = buttonColors) {
                Text("4", color = capybaraBrown)
            }
            Button(modifier = buttonModifier .border(2.dp, Brown1, shape = RoundedCornerShape(8.dp)), onClick = { onKeyClick("5") }, colors = buttonColors) {
                Text("5", color = capybaraBrown)
            }
            Button(modifier = buttonModifier .border(2.dp, Brown1, shape = RoundedCornerShape(8.dp)), onClick = { onKeyClick("6") }, colors = buttonColors) {
                Text("6", color = capybaraBrown)
            }
            Button(modifier = buttonModifier .border(2.dp, Brown1, shape = RoundedCornerShape(8.dp)) , onClick = { onKeyClick("×") }, colors = buttonColors) {
                Text("×", color = capybaraBrown)
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(modifier = buttonModifier .border(2.dp, Brown1, shape = RoundedCornerShape(8.dp)), onClick = { onKeyClick("1") }, colors = buttonColors) {
                Text("1", color = capybaraBrown)
            }
            Button(modifier = buttonModifier .border(2.dp, Brown1, shape = RoundedCornerShape(8.dp)), onClick = { onKeyClick("2") }, colors = buttonColors) {
                Text("2", color = capybaraBrown)
            }
            Button(modifier = buttonModifier .border(2.dp, Brown1, shape = RoundedCornerShape(8.dp)), onClick = { onKeyClick("3") }, colors = buttonColors) {
                Text("3", color = capybaraBrown)
            }
            Button(modifier = buttonModifier .border(2.dp, Brown1, shape = RoundedCornerShape(8.dp)), onClick = { onKeyClick("-") }, colors = buttonColors) {
                Text("-", color = capybaraBrown)
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(modifier = buttonModifier .border(2.dp, Brown1, shape = RoundedCornerShape(8.dp)), onClick = { onKeyClick(".") }, colors = buttonColors) {
                Text(".", color = capybaraBrown)
            }
            Button(modifier = buttonModifier .border(2.dp, Brown1, shape = RoundedCornerShape(8.dp)), onClick = { onKeyClick("0") }, colors = buttonColors) {
                Text("0", color = capybaraBrown)
            }
            Button(modifier = buttonModifier .border(2.dp, Brown1, shape = RoundedCornerShape(8.dp)), onClick = onDeleteClick, colors = buttonColors) {
                Text("⌫", color = capybaraBrown)
            }
            Button(modifier = buttonModifier .border(2.dp, Brown1, shape = RoundedCornerShape(8.dp)), onClick = { onKeyClick("+") }, colors = buttonColors) {
                Text("+", color = capybaraBrown)
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(modifier = buttonModifier .border(2.dp, Brown1, shape = RoundedCornerShape(8.dp)), onClick = onClearClick, colors = buttonColors) {
                Text("AC", color = capybaraBrown)
            }
            Button(modifier = buttonModifier .border(2.dp, Brown1, shape = RoundedCornerShape(8.dp)), onClick = onEqualsClick, colors = buttonColors) {
                Text("=", color = capybaraBrown)
            }
            Button(modifier = buttonModifier .border(2.dp, Brown1, shape = RoundedCornerShape(8.dp)), onClick = onOkClick, colors = buttonColors) {
                Text("OK", color = capybaraBrown)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemAdd(
    navController: NavController,
    viewModel: MainViewModel
) {
    val transactionType by viewModel.transactionType.collectAsState()
    var personalOrGroup by remember { mutableStateOf("個人") }
    val type by viewModel.transactionType.collectAsState()
    val amount by viewModel.amount.collectAsState()
    var amountInput by remember { mutableStateOf(amount.toString()) }
    val name by viewModel.name.collectAsState()
    val categories = TransactionCategory.entries.toTypedArray()
//    val category by viewModel.category.collectAsState()
//    var selectedCategory by remember { mutableStateOf(TransactionCategory.OTHER) }
    val selectedCategory by viewModel.category.collectAsState()
    val note by viewModel.note.collectAsState()
//    var groupName by remember { mutableStateOf("") }
    var splitMethod by remember { mutableStateOf("平均分攤") } // default 平攤
    var expandedGroup by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedPayers by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val keyboardController = LocalSoftwareKeyboardController.current

    var isBottomSheetVisible by remember { mutableStateOf(false) }
    var isKeyboardVisible by remember { mutableStateOf(false) }
    var toggleKeyboard by remember { mutableStateOf(false) }

    //Group
    val groups by viewModel.userGroups.collectAsState()
    var selectedGroup by remember { mutableStateOf(groups.firstOrNull()?.name ?: "groups") }
    var selectedGroupId by remember { mutableStateOf(groups.firstOrNull()?.id ?: "groups") }
    val shareMethod by viewModel.shareMethod.collectAsState()
    val dividers by viewModel.dividers.collectAsState()
    val payers by viewModel.payers.collectAsState()
    val groupMembers by viewModel.groupMembers.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showSnackbar by remember { mutableStateOf(false) }
    var showCustomBottomSheet by remember { mutableStateOf(false) }

    var expandedShareMethod by remember { mutableStateOf(false) }
    var expandedDividers by remember { mutableStateOf(false) }

    //by weiweihsu
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Helper to get a user's name by ID
    val getUserNameById: (String) -> String = { userId ->
        groupMembers.find { it.id == userId }?.name ?: "Unknown"
    }

    val focusManager = LocalFocusManager.current

    // Initialize the category to ensure it has a default value
    LaunchedEffect(Unit) {
        viewModel.loadUserGroups()
        viewModel.reloadUserData()
    }
    LaunchedEffect(Unit) {
        viewModel.setCategory(TransactionCategory.OTHER)
    }

    // 初始化時根據amount是否為整數決定顯示的內容
    LaunchedEffect(amount) {
        amountInput = if (amount % 1.0 == 0.0) {
            amount.toInt().toString()
        } else {
            amount.toString()
        }
    }


    Scaffold(modifier = Modifier
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MainBackgroundColor)  // 背景顏色
                .padding(innerPadding)
                .padding(top = 23.dp, start = 16.dp, end = 16.dp, bottom = 7.dp)
                .verticalScroll(rememberScrollState())
//                .clickable {
//                    if (isBottomSheetVisible) {
//                        coroutineScope.launch {
//                            isBottomSheetVisible = false
//                            toggleKeyboard = false
//                        }
//                    }
//                }
        ) {
            // 主要程式碼
            // (個人/群组)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { personalOrGroup = "個人" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (personalOrGroup == "個人") Brown1  else ButtonGrayColor
                    ),
                    elevation = ButtonDefaults.buttonElevation( // 設置按鈕陰影
                        defaultElevation = 8.dp,  // 默認陰影
                        pressedElevation = 12.dp, // 按下時的陰影
                        hoveredElevation = 10.dp, // 懸停時的陰影
                        focusedElevation = 10.dp  // 聚焦時的陰影
                )
                ) {
                    Text("個人",color = if (personalOrGroup == "個人") LightGray  else DarkGray, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
//                Button(
//                    onClick = { personalOrGroup = "群組" },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = if (personalOrGroup == "群組") Brown1 else ButtonGrayColor
//                    )
//                ) {
//                    Text("群組",color = if (personalOrGroup == "群組") LightGray  else DarkGray, fontSize = 20.sp, fontWeight = FontWeight.Bold)
//                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // if群組
            if (personalOrGroup == "群組") {
                ExposedDropdownMenuBox(
                    expanded = expandedGroup,
                    onExpandedChange = { expandedGroup = !expandedGroup }  // 使用 onExpandedChange 處理展開/收起狀態
                ) {
                    StylishTextField(
                        value = selectedGroup,
                        onValueChange = { selectedGroup = it },
                        label = "選擇群組",
                        readOnly = true,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth() // 移除 clickable 修飾符
                    )
                    ExposedDropdownMenu(
                        expanded = expandedGroup,
                        onDismissRequest = { expandedGroup = false }
                    ) {
                        groups.forEach { group ->
                            DropdownMenuItem(
                                text = { Text(text = group.name) },
                                onClick = {
                                    selectedGroup = group.name
                                    selectedGroupId = group.id
                                    expandedGroup = false
                                }
                            )
                        }
                    }
                }
            }
            else{
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { viewModel.setTransactionType("支出") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (transactionType == "支出") Orange1 else ButtonGrayColor
                        ),
                        elevation = if (transactionType == "支出") ButtonDefaults.buttonElevation(6.dp) else ButtonDefaults.buttonElevation(0.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "支出",color = if (transactionType == "支出")  VeryDarkGray  else DarkGray,fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(7.dp))

                    Button(
                        onClick = { viewModel.setTransactionType("收入") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (transactionType == "收入") Orange1 else ButtonGrayColor
                        ),
                        elevation =  if (transactionType == "收入") ButtonDefaults.buttonElevation(6.dp) else ButtonDefaults.buttonElevation(0.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "收入",color = if (transactionType == "收入") VeryDarkGray  else DarkGray,fontWeight = FontWeight.Bold)
                    }
                }
            }


            // 金額輸入
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                StylishTextField(
                    value = amountInput,
                    onValueChange = {
                        amountInput = it
                        it.toDoubleOrNull()?.let { validAmount ->
                            viewModel.setAmount(validAmount)
                            // 根據是否為整數來決定顯示的內容
                            amountInput = if (validAmount % 1.0 == 0.0) {
                                validAmount.toInt().toString()
                            } else {
                                validAmount.toString()
                            }
                        }
                    },
                    label = "金額",
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth(),
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable {
                            toggleKeyboard = !toggleKeyboard
                            isBottomSheetVisible = toggleKeyboard
                        }
                )
            }

            AnimatedVisibility(visible = isBottomSheetVisible) {
                CustomKeyboard(
                    onKeyClick = { key ->
                        amountInput += key
                        // 驗證並更新金額輸入
                        amountInput.toDoubleOrNull()?.let { validAmount ->
                            viewModel.setAmount(validAmount)
                        }
                    },
                    onDeleteClick = {
                        if (amountInput.isNotEmpty()) {
                            amountInput = amountInput.dropLast(1)
                            amountInput.toDoubleOrNull()?.let { validAmount ->
                                viewModel.setAmount(validAmount)
                            }
                        }
                    },
                    onClearClick = {
                        amountInput = ""
                        viewModel.setAmount(0.0)
                    },
                    onOkClick = {
                        coroutineScope.launch {
                            isBottomSheetVisible = false
                            toggleKeyboard = false
                        }
                    },
                    onEqualsClick = {
                        // 計算 amountInput
                        val result = evaluateExpression(amountInput)
                        amountInput = result.toString()
                        viewModel.setAmount(result)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(7.dp))

            // 名稱
            StylishTextField(
                value = name,
                onValueChange = { viewModel.setName(it) },
                label = "Name",
                readOnly = false,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(7.dp))

            if (personalOrGroup != "群組") {
                //類別
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory }
                ) {
                    StylishTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        label = "選擇類別",
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        modifier = Modifier.exposedDropdownSize(matchTextFieldWidth = true),
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    viewModel.setCategory(category)
                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(7.dp))
            }


            if (personalOrGroup == "群組") {

                // Payers selection
                ExposedDropdownMenuBox(
                    expanded = expandedPayers,
                    onExpandedChange = { expandedPayers = !expandedPayers }
                ) {
                    StylishTextField(
                        readOnly = true,
                        value = payers.joinToString(", ") { getUserNameById(it) },
                        onValueChange = { },
                        label = "付款人",
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedPayers,
                        onDismissRequest = { expandedPayers = false }
                    ) {
                        groupMembers.forEach { user ->
                            DropdownMenuItem(
                                text = { Text(user.name) },
                                onClick = {
                                    viewModel.togglePayer(user.id)
                                    expandedPayers = false
                                }
                            )
                        }
                    }
                }

                // Dividers selection
                ExposedDropdownMenuBox(
                    expanded = expandedDividers,
                    onExpandedChange = { expandedDividers = !expandedDividers }
                ) {
                    StylishTextField(
                        readOnly = true,
                        value = dividers.joinToString(", ") { getUserNameById(it) },
                        onValueChange = { },
                        label = "分帳的人",
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDividers,
                        onDismissRequest = { expandedDividers = false }
                    ) {
                        groupMembers.forEach { user ->
                            val isSelected = dividers.contains(user.id)
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = isSelected,
                                            onCheckedChange = {
                                                viewModel.toggleDivider(user.id)
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(user.name)
                                    }
                                },
                                onClick = {
                                    viewModel.toggleDivider(user.id)
                                }
                            )
                        }
                    }
                }

                // Share method selection
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .border(2.dp, colorResource(id = R.color.colorAccent), RoundedCornerShape(8.dp))
                        .background(colorResource(id = R.color.colorLight))
                        .clickable {
                            if (amountInput.isNotBlank() && dividers.isNotEmpty() && payers.isNotEmpty()) {
                                showCustomBottomSheet = true
                            } else {
                                showSnackbar = true
                            }
                        }
                ) {
                    Text(
                        text = if (shareMethod.isNotBlank()) "分帳方式 : $shareMethod" else "分帳方式 : 未選擇",
                        modifier = Modifier.padding(16.dp),
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Cursive,
                            color = Color.DarkGray
                        )
                    )
                }

                CustomBottomSheet(
                    isVisible = showCustomBottomSheet,
                    onDismiss = { showCustomBottomSheet = false }
                ) {
                    SeparateBottomSheetContent(
                        viewModel = viewModel,
                        groupId = selectedGroupId,
                        amount = amount.toFloat(),
                        onDismiss = { showCustomBottomSheet = false },
                        onComplete = {
                            showCustomBottomSheet = false
                        }
                    )
                }

                if (showSnackbar) {
                    LaunchedEffect(snackbarHostState) {
                        snackbarHostState.showSnackbar("請填寫所有必要的欄位")
                        showSnackbar = false
                    }
                }
            }

            Spacer(modifier = Modifier.height(5.dp))
            if (personalOrGroup != "群組") {
                //備註
                StylishTextField(
                    value = note,
                    onValueChange = { viewModel.setNote(it) },
                    label = "Note",
                    readOnly = false,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.weight(1f))
            }

            // 完成按鈕
            if(personalOrGroup == "群組"){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            errorMessage = "確定要清空"
                            showErrorDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Red,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.padding(end = 8.dp) // 與完成按鈕間距
                    ) {
                        Text("清空")
                    }

                    Button(
                        onClick = {
                        },
                        enabled = amountInput.isNotBlank() && name.isNotBlank() && amountInput.toDoubleOrNull() != 0.0,
                        modifier = Modifier
                    ) {
                        Text("完成")
                    }
                }
            }else{
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            // move to alertdialog
                            errorMessage = "確定要清空"
                            showErrorDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Red,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.padding(end = 8.dp) // 與完成按鈕間距
                    ) {
                        Text("清空")
                    }
                    Button(
                        onClick = {
                            viewModel.addPersonalTransaction()
                            amountInput = ""
                            viewModel.setAmount(0.0)
                            viewModel.setCategory(TransactionCategory.OTHER)
                            viewModel.setName("")
                            viewModel.setNote("")
                            navController.popBackStack()
                        },
                        elevation = ButtonDefaults.buttonElevation(2.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Green
                        ),
                        enabled = amountInput.isNotBlank() && name.isNotBlank() && amountInput.toDoubleOrNull() != 0.0,
                        modifier = Modifier
                    ) {
                        Text("完成")
                    }
                }
            }
            if (showErrorDialog) {
                AlertDialog(
                    onDismissRequest = { showErrorDialog = false },
                    title = { Text(text = "警告") },
                    text = { Text(text = errorMessage) },
                    confirmButton = {
                        Button(
                            onClick = {
                                showErrorDialog = false
                                if(personalOrGroup == "群組"){
                                    amountInput = ""
                                    viewModel.setAmount(0.0)
                                    viewModel.setCategory(TransactionCategory.OTHER)
                                    viewModel.setName("")
                                    viewModel.setNote("")
                                }else{
                                    amountInput = ""
                                    viewModel.setAmount(0.0)
                                    viewModel.setCategory(TransactionCategory.OTHER)
                                    viewModel.setName("")
                                    viewModel.setNote("")
                                }
                            }
                        ) {
                            Text("確定")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showErrorDialog = false }
                        ) {
                            Text("取消")
                        }
                    }
                )
            }
        }
    }

}


// Modified evaluateExpression function
fun evaluateExpression(expression: String): Double {
    val operators = Stack<Char>()
    val values = Stack<Double>()

    var i = 0
    while (i < expression.length) {
        when (val ch = expression[i]) {
            ' ' -> i++
            in '0'..'9', '.' -> {
                val sb = StringBuilder()
                while (i < expression.length && (expression[i] in '0'..'9' || expression[i] == '.')) {
                    sb.append(expression[i++])
                }
                values.push(sb.toString().toDouble())
            }
            '+', '-', '×', '÷' -> {
                while (!operators.isEmpty() && hasPrecedence(ch, operators.peek())) {
                    values.push(applyOp(operators.pop(), values.pop(), values.pop()))
                }
                operators.push(ch)
                i++
            }
            else -> throw IllegalArgumentException("Invalid character in expression")
        }
    }

    while (!operators.isEmpty()) {
        values.push(applyOp(operators.pop(), values.pop(), values.pop()))
    }

    return values.pop()
}

fun hasPrecedence(op1: Char, op2: Char): Boolean {
    if (op2 == '(' || op2 == ')') return false
    if ((op1 == '×' || op1 == '÷') && (op2 == '+' || op2 == '-')) return false
    return true
}

fun applyOp(op: Char, b: Double, a: Double): Double {
    return when (op) {
        '+' -> a + b
        '-' -> a - b
        '×' -> a * b
        '÷' -> if (b != 0.0) a / b else throw ArithmeticException("Cannot divide by zero")
        else -> throw IllegalArgumentException("Invalid operator")
    }
}
