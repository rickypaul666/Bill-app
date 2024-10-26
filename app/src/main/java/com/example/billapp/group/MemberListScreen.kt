package com.example.billapp.group

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.billapp.R
import com.example.billapp.data.models.User
import com.example.billapp.ui.theme.BillAppTheme
import com.example.billapp.ui.theme.BoxBackgroundColor
import com.example.billapp.ui.theme.Orange1
import com.example.billapp.viewModel.AvatarViewModel
import com.example.billapp.viewModel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberListScreen(
    navController: NavController,
    viewModel: MainViewModel,
    avatarViewModel: AvatarViewModel,
    groupId: String,
) {
    val members by viewModel.groupMembers.collectAsState()

    LaunchedEffect(groupId) {
        viewModel.getGroupMembers(groupId)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("群組成員", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Orange1
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BoxBackgroundColor)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(members) { member ->
                    MemberListItem(
                        member = member,
                        avatarViewModel = avatarViewModel
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            navController.navigate("Group_Invite/$groupId")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Orange1
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "+", fontSize = 24.sp, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun MemberListItem(
    member: User,
    avatarViewModel: AvatarViewModel
) {
    var imageUrl by remember { mutableStateOf("") }

    LaunchedEffect(member.id) {
        imageUrl = avatarViewModel.loadAvatar(member.id).toString()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .border(2.dp, Orange1, CircleShape)
            ) {
                if (imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = coil.request.ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.ic_user_place_holder),
                        contentDescription = "User Image",
                        error = painterResource(R.drawable.ic_user_place_holder),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.ic_user_place_holder),
                        contentDescription = "User Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = member.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp),
                fontSize = 20.sp,
                color = Color.Black
            )
        }
    }
}


