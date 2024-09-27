package com.example.billapp.group

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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

@Composable
fun GroupItem(groupName: String, createdBy: String, imageId: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = getImageResourceById(imageId)),
                contentDescription = stringResource(id = R.string.image_contentDescription),
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
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
    }
}

@Composable
fun GroupList(
    groupItems: List<Group>,
    onGroupClick: (String) -> Unit,
    navController: NavController
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(groupItems) { groupItem ->
            GroupItem(
                groupName = groupItem.name,
                createdBy = groupItem.createdBy,
                imageId = groupItem.imageId,
                onClick = { onGroupClick(groupItem.id) }
            )
        }
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
    }
}

@Preview
@Composable
fun GroupItemPreview() {
    GroupItem("Travel", "Jason", 1, {})
}