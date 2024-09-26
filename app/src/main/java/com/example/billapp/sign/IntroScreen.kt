package com.example.billapp.sign

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.billapp.R
import com.example.billapp.ui.theme.Brown5
import com.example.billapp.ui.theme.Brown6
import com.example.billapp.viewModel.MainViewModel

@Composable
fun IntroScreen(
    navController: NavController,
) {
    val montserratBold = FontFamily(Font(R.font.montserrat_bold))

    // Box to layer the background image
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.intro_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App name (TextView equivalent)
            Text(
                text = stringResource(id = R.string.app_name),
                fontFamily = montserratBold,
                fontSize = 50.sp,
                color = Brown5, // Color defined in your theme
                modifier = Modifier.padding(top = 40.dp)
            )

            // Capybara image
            Image(
                painter = painterResource(id = R.drawable.capybara),
                contentDescription = stringResource(id = R.string.image_contentDescription),
                modifier = Modifier
                    .padding(top = 30.dp)
                    .size(240.dp) // Adjust size if necessary
            )

            // Let's get started text
            Text(
                text = stringResource(id = R.string.let_s_get_started_text),
                fontFamily = montserratBold,
                fontSize = 25.sp,
                color = Brown5,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 25.dp)
            )

            // Sign In Button
            Button(
                onClick = { navController.navigate("signIn") },
                colors = ButtonDefaults.buttonColors(containerColor = Brown6), // Use your custom color
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .height(50.dp)
            ) {
                Text(stringResource(id = R.string.sign_in), color = Color.White)
            }

            // Sign Up Button (with border)
            Button(
                onClick = { navController.navigate("signUp") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = BorderStroke(2.dp, Brown6),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp)
                    .height(50.dp)
            ) {
                Text(stringResource(id = R.string.sign_up), color = Brown6)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun IntroScreenPreview() {
    val navController = rememberNavController()
    IntroScreen(navController)
}