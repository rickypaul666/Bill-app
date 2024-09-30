package com.example.billapp.dept_relation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.billapp.R
import com.example.billapp.models.DebtRelation
import com.example.billapp.viewModel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupedDeptRelationItem(
    viewModel: MainViewModel,
    fromUrl: String,
    toUrl: String,
    fromName: String,
    toName: String,
    totalAmount: Double,
    debtRelations: List<DebtRelation>,
    groupId: String
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(0.5f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp) // Adjust the size as needed
                            .padding(8.dp)
                    ) {
                        if (fromUrl != "") {
                            AsyncImage(
                                model = coil.request.ImageRequest.Builder(LocalContext.current)
                                    .data(fromUrl)
                                    .crossfade(true)
                                    .build(),
                                placeholder = painterResource(R.drawable.ic_user_place_holder),
                                contentDescription = "User Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.clip(CircleShape)
                            )
                        }else{
                            Image(
                                painter = painterResource(R.drawable.ic_user_place_holder),
                                contentDescription = "User Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.clip(CircleShape)
                            )
                        }
                    }
                    Text(text = fromName, style = MaterialTheme.typography.bodyLarge)
                }
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Arrow"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(0.5f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp) // Adjust the size as needed
                            .padding(8.dp)
                    ) {
                        if (toUrl != "") {
                            AsyncImage(
                                model = coil.request.ImageRequest.Builder(LocalContext.current)
                                    .data(toUrl)
                                    .crossfade(true)
                                    .build(),
                                placeholder = painterResource(R.drawable.ic_user_place_holder),
                                contentDescription = "User Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.clip(CircleShape)
                            )
                        }
                        else {
                            Image(
                                painter = painterResource(R.drawable.ic_user_place_holder),
                                contentDescription = "User Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.clip(CircleShape)
                            )
                        }
                    }
                    Text(text = toName, style = MaterialTheme.typography.bodyLarge)
                }
                Text(
                    text = "$${String.format("%.2f", totalAmount)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (expanded) {
                debtRelations.forEach { relation ->
                    DeptRelationDetailItem(
                        viewModel = viewModel,
                        debtRelation = relation,
                        groupId = groupId,
                    )
                }
            }
        }
    }
}
