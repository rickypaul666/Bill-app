package com.example.billapp.dept_relation

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.billapp.viewModel.MainViewModel
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.billapp.viewModel.AvatarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtRelationsScreen(
    avatarViewModel: AvatarViewModel,
    viewModel: MainViewModel,
    groupId: String,
    onBackPress: () -> Unit
) {
    val deptRelation by viewModel.debtRelations.collectAsState()
    val groupIdDeptRelations by viewModel.groupIdDebtRelations.collectAsState()

    // Load transactions and calculate dept relations when the screen is opened
    LaunchedEffect(groupId) {
        viewModel.loadGroupDeptRelations(groupId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "債務關係",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFB67B6C)
                )
            )
        }
    ) { innerPadding ->
        DeptRelationList(
            avatarViewModel = avatarViewModel,
            viewModel = viewModel,
            debtRelations = groupIdDeptRelations,
            groupId = groupId,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
