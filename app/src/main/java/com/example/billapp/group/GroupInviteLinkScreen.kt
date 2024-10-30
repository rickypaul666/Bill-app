package com.example.billapp.group

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.billapp.R
import com.example.billapp.ui.theme.ButtonRedColor
import com.example.billapp.ui.theme.MainBackgroundColor
import com.example.billapp.ui.theme.Orange1
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.common.BitMatrix
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupInviteLinkScreen(
    groupId: String,
    navController: NavController
) {
    val context = LocalContext.current
    val inviteLink = groupId
    val qrCodeBitmap = remember { generateQRCode(groupId) }

    val writePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                qrCodeBitmap?.let {
                    saveImageToGallery(context, it)
                    Toast.makeText(context, "已下載到相簿", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "需要手機權限來保存照片", Toast.LENGTH_SHORT).show()
            }
        }
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackgroundColor),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "群組邀請連結",
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 24.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Orange1)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MainBackgroundColor)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.capybara_take_invitation),
                contentDescription = "Group Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 16.dp)
            )

            Text(
                text = "邀請連結： $inviteLink",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Button(
                onClick = {
                    copyToClipboard(context, inviteLink)
                    Toast.makeText(context, "已複製到剪貼簿", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange1,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_content_copy_24),
                    contentDescription = "Copy",
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("複製連結")
            }

            Spacer(modifier = Modifier.height(24.dp))

            qrCodeBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "QR Code",
                    modifier = Modifier
                        .size(220.dp)
                        .padding(vertical = 8.dp)
                )

                Button(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ||
                            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                            saveImageToGallery(context, it)
                            Toast.makeText(context, "已下載到相簿", Toast.LENGTH_SHORT).show()
                        } else {
                            writePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Orange1,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_download_24),
                        contentDescription = "Download"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("下載 QR Code")
                }
            }
        }
    }
}

fun generateQRCode(content: String): Bitmap? {
    val writer = QRCodeWriter()
    val bitMatrix: BitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 200, 200)
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
        }
    }
    return bitmap
}

fun copyToClipboard(context: Context, text: String) {
    val clipboard = ContextCompat.getSystemService(context, android.content.ClipboardManager::class.java)
    val clip = android.content.ClipData.newPlainText("Group Invite Link", text)
    clipboard?.setPrimaryClip(clip)
}

fun saveImageToGallery(context: Context, bitmap: Bitmap) {
    val filename = "QRCode_${System.currentTimeMillis()}.png"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // 使用 MediaStore 儲存圖片
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/QRCode")
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            context.contentResolver.openOutputStream(uri).use { outputStream ->
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    Toast.makeText(context, "已下載到相簿", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "保存圖片失敗", Toast.LENGTH_SHORT).show()
                }
            }
        }
    } else {
        // Android 9 及以下的情況
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename)
        try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
            Toast.makeText(context, "已下載到相簿", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "保存圖片失敗", Toast.LENGTH_SHORT).show()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupInviteLinkScreenPreview() {
    // Create a mock NavController
    val navController = rememberNavController()

    // Call the composable you want to preview
    GroupInviteLinkScreen(
        groupId = "mockGroupId",
        navController = navController
    )
}