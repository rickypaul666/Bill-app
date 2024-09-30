package com.example.billapp.group

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.billapp.CustomBottomSheet
import com.example.billapp.R
import com.example.billapp.dept_relation.GroupedDeptRelationItem
import com.example.billapp.ui.theme.Green
import com.example.billapp.ui.theme.Orange4
import com.example.billapp.ui.theme.Purple40
import com.example.billapp.ui.theme.BoxBackgroundColor
import com.example.billapp.ui.theme.BottomBackgroundColor
import com.example.billapp.ui.theme.MainBackgroundColor
import com.example.billapp.ui.theme.Orange1
import com.example.billapp.ui.theme.HightlightWhiteColor
import com.example.billapp.ui.theme.Brown5
import com.example.billapp.viewModel.AvatarViewModel
import com.example.billapp.viewModel.MainViewModel


@Composable
fun GroupSettingScreen(
    groupId: String,
    viewModel: MainViewModel,
    totalDebt: Double,
    avatarViewModel: AvatarViewModel,
    navController: NavController
) {
    // State for managing the visibility of the bottom sheet
    val isBottomSheetVisible = remember { mutableStateOf(false) }

    val group by viewModel.getGroup(groupId).collectAsState(initial = null)
    val deptRelations by viewModel.groupIdDebtRelations.collectAsState()
    val currentUser by viewModel.user.collectAsState()
    val currentUserId = currentUser?.id ?: ""

    LaunchedEffect(groupId) {
        viewModel.getGroupDeptRelations(groupId)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            androidx.compose.material.TopAppBar(
                title = { androidx.compose.material.Text(group?.name ?: "Group Detail", color = Color.White) },
                navigationIcon = {
                    androidx.compose.material.IconButton(onClick = { navController.navigateUp() }) {
                        androidx.compose.material.Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // "管理" button on the right
                    Button(
                        onClick = { isBottomSheetVisible.value = true }, // Set this to toggle bottom sheet visibility
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White // Set button text color to white
                        )
                    ) {
                        Text(text = "管理")
                    }
                },
                backgroundColor = Orange1
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MainBackgroundColor)
        ) {
            // Row to hold Image and Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {

                // Image with a circular frame
                Image(
                    painter = painterResource(id = getImageResourceById(1)), //要修改
                    contentDescription = stringResource(id = R.string.image_contentDescription),
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(
                            BorderStroke(2.dp, Color.Gray), // Circular border with 2dp width and gray color
                            shape = CircleShape
                        )
                        .background(color = Purple40),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(8.dp)) // Space between image and card

                // Debt Relationship Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Let the Card take the remaining space
                    elevation = 4.dp,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(2.dp, Color.Gray),
                    backgroundColor = BoxBackgroundColor
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            //Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "您尚有",
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 20.sp,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 20.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .width(150.dp) // 固定寬度
                                    .padding(4.dp) // 調整內邊距
                                    .background(
                                        color = when {
                                            totalDebt < 0 -> Color(0xF3FF8B8B) // 負數時為紅色
                                            totalDebt > 0 -> Green // 正數時為綠色
                                            else -> Orange4 // 0 為淺黃色
                                        },
                                        shape = RoundedCornerShape(8.dp) // 圓角背景
                                    ),
                                contentAlignment = Alignment.BottomStart
                            ) {
                                Text(
                                    text = when {
                                        totalDebt < 0 -> "應付 : ${-totalDebt}" // 負數時為紅色
                                        totalDebt > 0 -> "應收 : $totalDebt" // 正數時為綠色
                                        else -> "帳務已結清" // 0 為淺黃色
                                    },
                                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                                    modifier = Modifier.padding(8.dp) // 調整文字的內邊距
                                )
                            }
                        }

                        Button(
                            onClick = {
                                navController.navigate("deptRelationsScreen/$groupId")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HightlightWhiteColor  // Use containerColor instead of backgroundColor
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Text(
                                text = "查看所有債務關係",
                                color = Color.Black
                            )
                        }
                    }

//                     val relevantDeptRelations = deptRelations.values.flatten()
//                         .filter { it.from == currentUserId || it.to == currentUserId }
//                         .take(2)

//                     relevantDeptRelations.forEach { relation ->
//                         var fromName by remember { mutableStateOf("") }
//                         var toName by remember { mutableStateOf("") }
//                         var fromUrl by remember { mutableStateOf("") }
//                         var toUrl by remember { mutableStateOf("") }

//                         LaunchedEffect(relation.from, relation.to) {
//                             fromName = viewModel.getUserName(relation.from)
//                             toName = viewModel.getUserName(relation.to)
//                             fromUrl = avatarViewModel.loadAvatar(relation.from).toString()
//                             toUrl = avatarViewModel.loadAvatar(relation.to).toString()
//                         }

//                         GroupedDeptRelationItem(
//                             viewModel = viewModel,
//                             fromName = fromName,
//                             toName = toName,
//                             fromUrl = relation.from,
//                             toUrl = relation.to,
//                             totalAmount = relation.amount,
//                             debtRelations = listOf(relation),
//                             groupId = groupId
//                         )
//                     }

//                     // Button to view all debt relations
//                     Button(
//                         onClick = {
//                             navController.navigate("deptRelationsScreen/$groupId")
//                         },
//                         modifier = Modifier
//                             .fillMaxWidth()
//                             .padding(top = 8.dp)
//                     ) {
//                         Text("查看所有債務關係")
//                     }
                }
            }

            // New Box with Title "近期交易" and Button in TopBar style
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .background(Color.LightGray) // Change background color as desired
                    .border(
                        BorderStroke(2.dp, Color.Gray), // Circular border with 2dp width and blue color
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(
                        color = BoxBackgroundColor,
                        shape = RoundedCornerShape(8.dp) // 圓角背景
                    ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "近期交易",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 20.sp,
                        color = Color.Black, // Text color, can be customized
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 56.dp)
                    )

                    Button(
                        onClick = {
                            navController.navigate("groupTest/$groupId")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HightlightWhiteColor
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "新增交易",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black
                        )
                    }
                }
            }
            // Add ManagementBottomSheet call here
            ManagementBottomSheet(
                isVisible = isBottomSheetVisible.value,
                onDismiss = { isBottomSheetVisible.value = false }, // Set visibility to false to dismiss
                groupId = groupId,
                viewModel = viewModel,
                navController = navController
            )
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
                .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                .background(Color.LightGray) // You can set the background color as desired
                .border(BorderStroke(2.dp, Color.Gray), shape = RoundedCornerShape(8.dp)) // Optional border
                .heightIn(min = 200.dp, max = 400.dp),// Set minimum and maximum height here
        ) {

            Button(
                onClick = {
                    navController.navigate("memberListScreen/$groupId")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Brown5
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            ) {
                Text(text = "成員")
            }

            Button(
                onClick = {
                    navController.navigate("Group_Invite/$groupId")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Brown5
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "群組邀請連結")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    groupId?.let {
                        viewModel.deleteGroup(groupId)
                        navController.navigateUp()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "Delete Group"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "刪除群組")
            }
        }
    }
}
