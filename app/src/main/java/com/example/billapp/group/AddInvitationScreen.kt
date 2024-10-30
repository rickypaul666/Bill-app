package com.example.billapp.group

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.billapp.R
<<<<<<< HEAD
import com.example.billapp.data.models.User
import com.example.billapp.ui.theme.Brown1
import com.example.billapp.ui.theme.Brown2
import com.example.billapp.ui.theme.Brown5
import com.example.billapp.ui.theme.Brown6
import com.example.billapp.ui.theme.Brown7
=======
import com.example.billapp.ui.theme.*
>>>>>>> origin/QR_code_update
import com.example.billapp.viewModel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInvitationScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    var groupLink by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    val currentUser = viewModel.user.collectAsState().value

    val userId by remember { mutableStateOf(currentUser?.id ?: "") }

    // Handle QR code scan result
    val qrCodeResult = navController.currentBackStackEntry?.savedStateHandle?.get<String>("qrCodeResult")
    qrCodeResult?.let {
        groupLink = it
        navController.currentBackStackEntry?.savedStateHandle?.remove<String>("qrCodeResult")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("加入群組", color = PrimaryFontColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("qrCodeScanner")
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_qr_code_scanner_24),
                            contentDescription = "掃描 QR code",
                            tint = PrimaryFontColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(MainCardRedColor)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MainBackgroundColor)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MainBackgroundColor),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextField(
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = MainBackgroundColor, // 背景顏色
                        focusedIndicatorColor = Brown6, // 焦點下的指示器顏色
                        unfocusedIndicatorColor = ItemAddMainColor, // 未焦點下的指示器顏色
                        errorIndicatorColor = MaterialTheme.colorScheme.error, // 錯誤狀態下的指示器顏色
                    ),
                    value = groupLink,
                    onValueChange = {
                        groupLink = it
                        isError = groupLink.isBlank()
                    },
                    label = { Text("群組連結", color = PrimaryFontColor) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
                )
                if (isError) {
                    Text(
                        text = "群組連結不能為空",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (groupLink.isNotBlank()) {
                            viewModel.checkGroupExists(groupLink) { groupExists ->
                                if (groupExists) {
                                    viewModel.checkUserInGroup(groupLink, userId) { userInGroup ->
                                        if (userInGroup) {
                                            dialogMessage = "您已加入該群組"
                                        } else {
                                            viewModel.assignUserToGroup(groupLink, userId)
                                            viewModel.updateUserExperience(userId, 10)
                                            dialogMessage = "成功加入群組"
                                        }
                                        showDialog = true
                                    }
                                } else {
                                    dialogMessage = "查無此ID"
                                    showDialog = true
                                }
                            }
                        } else {
                            isError = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonRedColor,
                        contentColor = Color.White // 根據需要調整文本顏色
                    ),
                    enabled = groupLink.isNotBlank()
                ) {
                    Text("完成")
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("確定", color = PrimaryFontColor)
                }
            },
            title = { Text("提示", color = PrimaryFontColor) },
            text = { Text(dialogMessage, color = PrimaryFontColor) },
            containerColor = MainBackgroundColor
        )
    }
}

@Preview
@Composable
fun AddInvitationScreenPreview() {
    // Create a mock NavController
    val navController = rememberNavController()
    // Create a mock or default MainViewModel
    val viewModel = MainViewModel() // You may need to provide required parameters or use a factory if necessary
    AddInvitationScreen(navController = navController, viewModel = viewModel)
}