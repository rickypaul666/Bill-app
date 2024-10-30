package com.example.billapp

import AvatarScreen
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billapp.ui.theme.theme.BoxBackgroundColor
import com.example.billapp.ui.theme.theme.ButtonGreenColor
import com.example.billapp.ui.theme.theme.ButtonRedColor
import com.example.billapp.ui.theme.theme.MainBackgroundColor
import com.example.billapp.viewModel.AvatarViewModel
import com.example.billapp.viewModel.MainViewModel
import androidx.compose.ui.graphics.graphicsLayer
import com.example.billapp.home.StylishTextField


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: MainViewModel,
    avatarViewModel: AvatarViewModel,
    requestPermission: (String) -> Unit
) {
    val user by viewModel.user.collectAsState()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf(viewModel.getUserBudget().toString()) } // Add budget state
    var isEditing by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(user) {
        user?.let {
            name = it.name
            email = it.email
            budget = it.budget.toString() // Initialize budget from user data
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的個人檔案") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = MainBackgroundColor)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = BoxBackgroundColor
                        )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()), // Enable scrolling for overflow
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(150.dp) // Adjust the size as needed
                            .padding(8.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        AvatarScreen(avatarViewModel)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    StylishTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Name",
                        readOnly = !isEditing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer(alpha = if (isEditing) 1f else 0.5f)

                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StylishTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer(alpha = if (isEditing) 1f else 0.5f),
                        readOnly = !isEditing,
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Add the budget input field
                    StylishTextField(
                        value = budget,
                        onValueChange = { budget = it },
                        label = "Budget",
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer(alpha = if (isEditing) 1f else 0.5f),
                        readOnly = !isEditing,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // Ensure numeric input
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (isEditing) {
                                user?.let {
                                    val updatedUser = it.copy(
                                        name = name,
                                        email = email,
                                        budget = budget.toIntOrNull() ?: 0 // Convert budget to int
                                    )
                                    viewModel.updateUserProfile(updatedUser)
                                    imageUri?.let { it1 -> avatarViewModel.uploadAvatar(it1) }

                                    // Update budget in Firestore
                                    viewModel.updateUserBudget(updatedUser.budget)
                                }
                            }
                            isEditing = !isEditing
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                                containerColor = if (isEditing) ButtonRedColor else ButtonGreenColor
                                )
                    ) {
                        Text(if (isEditing) "Update" else "Edit")
                    }
                }
            }
        }
    }
}
