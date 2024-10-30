package com.example.billapp.group

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.billapp.ui.theme.Orange1
import com.example.billapp.viewModel.MainViewModel

// 下方群組圖示點擊後會導到 GroupScreen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreen(
    navController: NavController,
    viewModel: MainViewModel,
) {
    LaunchedEffect(Unit) {
        viewModel.loadUserGroups()
        viewModel.reloadUserData()
    }
    val groups by viewModel.userGroups.collectAsState()

    // 使用 Box 設置整頁背景顏色
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(120.dp),
                colors = topAppBarColors(
                    containerColor = Color(0xFFE4DFCB),
                    titleContentColor = Color(0xFF000000),
                ),
                title =  {
                    Button(
                        onClick = { navController.navigate("CreateGroupScreen") },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Orange1
                        ),
                        modifier = Modifier
                            .padding(top = 30.dp, end = 16.dp, bottom = 30.dp)
                    ) {
                        Text(
                            text = "新增群組",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = { navController.navigate("Join_Group") },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Orange1 // Use containerColor instead of backgroundColor
                        ),
                        modifier = Modifier
                            .padding(top = 30.dp, end = 16.dp, bottom = 30.dp)
                    ) {
                        Text(
                            text = "加入群組",  // Join Group
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Group list
            GroupList(
                viewModel = viewModel,
                groupItems = groups,
                onGroupClick = { groupId -> navController.navigate("groupDetail/$groupId") },
                navController
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GroupScreenPreview() {
    val navController = rememberNavController()
    val viewModel = MainViewModel()
    GroupScreen(navController, viewModel)
}

