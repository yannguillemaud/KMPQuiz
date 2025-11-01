package ygmd.kmpquiz.ui.composable.createquiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
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
import androidx.compose.ui.unit.dp
import ygmd.kmpquiz.ui.theme.Dimens

@Composable
fun <T> SelectCard(
    items: List<T>,
    modifier: Modifier = Modifier,
    title: String,
    initialSelected: T?,
    itemLabel: (T) -> String,
    onSelectionChanged: (T?) -> Unit = {},
) {
    var isExpended by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(initialSelected) }

    Card(
        modifier = modifier.fillMaxWidth().animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.CardElevation),
        onClick = { isExpended = !isExpended }
    ) {
        Column(modifier = Modifier.padding(Dimens.PaddingMedium))
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = selectedItem?.let { itemLabel(it) } ?: "None",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (isExpended) "Fold" else "Expand",
                )
            }

            AnimatedVisibility(visible = isExpended) {
                Column {
                    Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
                    FlowColumn(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
                    ) {
                        items.forEach { item ->
                            val isSelected = selectedItem == item
                            ElevatedFilterChip(
                                selected = isSelected,
                                label = { Text(text = itemLabel(item)) },
                                leadingIcon = {
                                    if (isSelected) Icon(
                                        Icons.Default.Check,
                                        contentDescription = "selected item"
                                    ) else Spacer(Modifier.width(24.dp))
                                },
                                onClick = {
                                    selectedItem = if (item == selectedItem) null else item
                                    onSelectionChanged(selectedItem)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
