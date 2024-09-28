package com.example.billapp.group

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billapp.R
import com.example.billapp.models.Group
import com.example.billapp.ui.theme.ButtonRedColor
import com.example.billapp.ui.theme.Purple40


@Composable
fun GroupItem(groupName: String, createdBy: String, totalDebt: Float, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFBBB0A2))
                .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 8.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = getImageResourceById(imageId)),
                contentDescription = stringResource(id = R.string.image_contentDescription),
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(color = Purple40),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                BasicText(
                    text = groupName,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                BasicText(
                    text = "created by : $createdBy",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        //Spacer(modifier = Modifier.height(0.dp)) // 縮短距離，根據需要調整此值

        Row(
            modifier = Modifier
                .fillMaxWidth() // 填滿整個寬度
                .background(Color(0xFFBBB0A2))
                .padding(4.dp), // 外邊距
            horizontalArrangement = Arrangement.End // 內容向右對齊
        ) {
            Box(
                modifier = Modifier
                    .width(150.dp) // 固定寬度
                    .padding(top = 16.dp, start = 4.dp, end = 4.dp, bottom = 4.dp) // 加入上方的內邊距
                    .background(color = ButtonRedColor, shape = RoundedCornerShape(8.dp)), // 圓角背景顏色
                contentAlignment = Alignment.BottomStart // 內容對齊到左下角
            ) {
                Text(
                    text = "總欠債: $totalDebt NTD",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                )
            }
        }
    }
}

@Composable
fun GroupList(
    groupItems: List<Group>,
    onGroupClick: (String) -> Unit,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE4DFCB)) // 設置整個頁面的背景顏色
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(groupItems) { groupItem ->
                GroupItem(
                    groupName = groupItem.name,
                    createdBy = groupItem.createdBy,
                    totalDebt = 10000f, // 假設你有這個數據，這裡使用示例值
                    onClick = { onGroupClick(groupItem.id) }
                )
            }
            /*
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    navController.navigate("CreateGroupScreen")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(vertical = 4.dp)
            ) {
                Text("新增群組")
            }
        }
        */
        }
    }
}

@Preview
@Composable
fun GroupItemPreview()
{
    GroupItem("Travel","Jason",10000f,{})
}