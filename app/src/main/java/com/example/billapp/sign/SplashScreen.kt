package com.example.billapp.sign

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billapp.R
import com.example.billapp.viewModel.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Immediate data loading if user is logged in
    LaunchedEffect(isUserLoggedIn, user) {
        if (isUserLoggedIn && user != null) {
            viewModel.loadUserData(user!!.id) // Start loading immediately
            viewModel.loadUserTransactions()
            viewModel.loadUserGroups()
        }
    }

    // Ensure splash is shown for at least 0.5 seconds
    LaunchedEffect(isLoading) {
        if (!isLoading) {
            delay(500)  // Add a minimum splash time of 0.5s
            if (isUserLoggedIn && user != null) {
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                }
            } else {
                navController.navigate("intro") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
    }

    // UI: Display splash screen with a loading indicator
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center) // Use the correct Alignment type here
                    .size(48.dp),
                color = MaterialTheme.colors.primary,
                strokeWidth = 4.dp
            )
        }
    }
}