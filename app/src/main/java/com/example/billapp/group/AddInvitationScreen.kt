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
import com.example.billapp.QRCodeScannerScreen
import com.example.billapp.R
import com.example.billapp.data.models.User
import com.example.billapp.ui.theme.Brown1
import com.example.billapp.ui.theme.Brown2
import com.example.billapp.ui.theme.Brown5
import com.example.billapp.ui.theme.Brown6
import com.example.billapp.ui.theme.Brown7
import com.example.billapp.viewModel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInvitationScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    var groupLink by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val currentUser = viewModel.user.collectAsState().value

    val userId by remember { mutableStateOf(currentUser?.id ?: "") }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("加入群組") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("qrCodeScanner")
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_qr_code_scanner_24),
                            contentDescription = "掃描 QR code"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(Color(0xFF9B7160))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .background(Color(0xFFFFFAF1)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {
            TextField(
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFCCBEA3), // 背景顏色
                    focusedIndicatorColor = Brown6, // 焦點下的指示器顏色
                    unfocusedIndicatorColor = Color(0xFFCCBEA3), // 未焦點下的指示器顏色
                    errorIndicatorColor = MaterialTheme.colorScheme.error, // 錯誤狀態下的指示器顏色
                ),
                value = groupLink,
                onValueChange = {
                    groupLink = it
                    isError = groupLink.isBlank()
                },
                label = { Text("群組連結") },
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
                        // 根據 grouplink(groupid) 將當前的User新增到該群組的 assignedTo
                        viewModel.assignUserToGroup(groupLink, currentUser?.id ?: "")
                        viewModel.updateUserExperience(userId,10)
                        navController.popBackStack()
                    } else {
                        isError = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Brown5,
                    contentColor = Color.White // 根據需要調整文本顏色
                ),
                enabled = groupLink.isNotBlank()
            ) {
                Text("完成")
            }
        }
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