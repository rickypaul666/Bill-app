package com.example.billapp

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billapp.viewModel.MainViewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip

@Composable
fun SettingScreen(
    navController: NavController,
    viewModel: MainViewModel,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color(0xFFE4DFCB))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 标题部分
        Text(
            text = "設定",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            color = Color.Black
        )

        // 头像和进度条部分，添加边框
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(bottom = 24.dp)
                .border(2.dp, Color.Black, RoundedCornerShape(16.dp)) // 设置边框和圆角
                .padding(16.dp) // 给内容留一些内边距
        ) {
            // 头像部分
            Image(
                painter = painterResource(R.drawable.avatar_placeholder_2),
                contentDescription = "Character Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp) // 设置头像大小
                    .clip(CircleShape) // 设置头像为圆形
                    .background(Color.Gray) // 头像背景颜色
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 名字和笔图标部分
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 人物名称
                Text(
                    text = "AMY",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))

                // 笔的图标
                // 可点击的笔图标
                Icon(
                    painter = painterResource(id = R.drawable.baseline_edit_24), // 使用你的笔图标资源
                    contentDescription = "Edit Icon",
                    modifier = Modifier
                        .size(16.dp)
                        .clickable(onClick = {
                            // 点击后执行的操作，例如导航到编辑页面
                           // navController.navigate("edit_profile") // 确保在此处替换为你的目标页面
                        }),
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 进度条部分
            ParallelogramProgressBar(
                TargetProgress = 1f,
                text = "信譽等級: 5/5",
                color = Color.Green,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            ParallelogramProgressBar(
                TargetProgress = 0.5f,
                text = "社交值: 等級: LV50/100",
                color = Color.Blue,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            ParallelogramProgressBar(
                TargetProgress = 0.083f,
                text = "血條: 2500/30000",
                color = Color.Red,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }

        // 开关按钮
        SwitchWithLabelPreview()

        // 按钮部分
        Spacer(modifier = Modifier.height(16.dp))

        SettingButton(
            text = "個人資料修改",
            icon = painterResource(id = R.drawable.baseline_person_24),
            onClick = {
                navController.navigate("profile")
            }
        )

        SettingButton(
            text = "聯絡我們",
            icon = painterResource(id = R.drawable.baseline_call_24),
            onClick = {
                navController.navigate("contact_us")
            }
        )

        SettingButton(
            text = "問題回報",
            icon = painterResource(id = R.drawable.baseline_report_problem_24),
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://forms.gle/gi6Fyew6qfFyVAn29"))
                context.startActivity(intent)
            }
        )

        SettingButton(
            text = "關於",
            icon = painterResource(id = R.drawable.baseline_speaker_notes_24),
            onClick = {
                navController.navigate("about")
            }
        )
    }
}

@Composable
fun SettingButton(
    text: String,
    icon: Painter,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .size(height = 60.dp, width = 200.dp), // 设置按钮高度和宽度
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(Color(0xFFBCBCBC))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 左侧的图标
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .weight(1f), // 确保图标靠左
                tint = Color.Black
            )

            // 右侧的文本
            Text(
                text = text,
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(8f) // 确保文本占据剩余空间
            )
        }
    }
}


@Composable
fun CustomSwitchWithLabel(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val switchWidth = 60.dp  // 开关的宽度
    val thumbSize = 30.dp    // 按钮的大小
    val thumbOffset = if (checked) (switchWidth - thumbSize) else 0.dp  // 计算按钮的位置

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ) {
        // 图标
        Icon(
            painter = painterResource(id = R.drawable.baseline_circle_notifications_24), // 使用系统图标，你可以替换为自己的图标
            contentDescription = "Notification Icon",
            tint = Color.Yellow,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(8.dp)) // 图标和文字之间的间距

        // 通知文字
        Text(
            text = if (checked) "通知已開啟" else "通知已關閉",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.width(16.dp)) // 文字和开关之间的间距

        // 自定义开关
        Row(
            modifier = Modifier
                .size(width = switchWidth, height = 30.dp)
                .background(
                    color = if (checked) Color(0xFF8A7059) else Color(0xFFB0BEC5),
                    shape = RoundedCornerShape(50)
                )
                .clickable { onCheckedChange(!checked) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .size(thumbSize)
                    .background(Color.White, shape = CircleShape)
            )
        }
    }
}

@Composable
fun SwitchWithLabelPreview() {
    var isChecked by remember { mutableStateOf(false) }

    CustomSwitchWithLabel(
        checked = isChecked,
        onCheckedChange = { isChecked = it }
    )
}
