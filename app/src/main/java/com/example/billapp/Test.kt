import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billapp.data.models.Group
import com.example.billapp.viewModel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdown(
    navController: NavController,
    viewModel: MainViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.loadUserGroups()
        viewModel.reloadUserData()
    }

    var expanded by remember { mutableStateOf(false) }
    val groups by viewModel.userGroups.collectAsState()
    var selectedGroup by remember {
        mutableStateOf(groups.firstOrNull()?.name ?: "groups")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Transaction") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }  // 使用 onExpandedChange 處理展開/收起狀態
            ) {
                TextField(
                    value = selectedGroup,
                    onValueChange = { selectedGroup = it },
                    label = { Text("選擇群組") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth() // 移除 clickable 修飾符
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    groups.forEach { group ->
                        DropdownMenuItem(
                            text = { Text(text = group.name) },
                            onClick = {
                                selectedGroup = group.name
                                expanded = false
                                // 可以在這裡添加額外的動作，如導航等
                            }
                        )
                    }
                }
            }
        }
    }
}
