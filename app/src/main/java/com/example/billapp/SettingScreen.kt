package com.example.billapp

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billapp.viewModel.MainViewModel

val lightBrown = Color(0xFF6D4C41)

@Composable
fun SettingScreen(
    navController: NavController,
    viewModel: MainViewModel,
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE4DFCB)) // 整体背景颜色
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 顶部标题栏，使用深咖啡色背景
            TopAppBar(
                title = { Text("設定", style = MaterialTheme.typography.h5) },
                backgroundColor = Color(0xFF6D4C41), // 深咖啡色
                elevation = 4.dp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 头像和进度条部分
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.avatar_placeholder_2),
                        contentDescription = "Character Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colors.primary, CircleShape)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 人物名称和编辑图标
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "AMY", style = MaterialTheme.typography.h6)
                        IconButton(onClick = { /* Navigate to edit profile */ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_edit_24),
                                contentDescription = "Edit Profile",
                                tint = lightBrown
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    ProgressBar("信譽等級", 1f, "5/5", Color.Green)
                    Spacer(modifier = Modifier.height(6.dp))
                    ProgressBar("社交值", 0.5f, "等級: LV50/100", Color.Blue)
                    Spacer(modifier = Modifier.height(6.dp))
                    ProgressBar("血條", 0.083f, "2500/30000", Color.Red)
                }
            }

            NotificationSwitch()

            SettingButton(
                text = "個人資料修改",
                icon = painterResource(id = R.drawable.baseline_person_24),
                onClick = { navController.navigate("profile") }
            )
            SettingButton(
                text = "聯絡我們",
                icon = painterResource(id = R.drawable.baseline_call_24),
                onClick = { navController.navigate("contact_us") }
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
                onClick = { navController.navigate("about") }
            )

            Spacer(modifier = Modifier.weight(1f)) // 将登出按钮推至底部

            Button(
                onClick = { /* 执行登出操作 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
            ) {
                Text(text = "登出", color = Color.White)
            }
        }
    }
}

@Composable
fun NotificationSwitch() {
    var isChecked by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp), // 调整高度以减少空间占用
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_circle_notifications_24),
            contentDescription = "Notification Icon",
            tint = lightBrown
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = if (isChecked) "通知已開啟" else "通知已關閉",
            style = MaterialTheme.typography.body1
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = isChecked,
            onCheckedChange = { isChecked = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFFFFFFFF),
                checkedTrackColor = lightBrown
            )
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
            .padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFBBB0A2)),
        elevation = ButtonDefaults.elevation(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painter = icon, contentDescription = null, tint = lightBrown)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = text, style = MaterialTheme.typography.button, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun ProgressBar(label: String, progress: Float, text: String, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.subtitle1)
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            backgroundColor = Color.LightGray
        )
        Text(text = text, style = MaterialTheme.typography.caption, modifier = Modifier.align(Alignment.End))
    }
}
