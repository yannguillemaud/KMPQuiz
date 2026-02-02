import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
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
fun <T> MultiSelectCard(
    items: List<T>,
    modifier: Modifier = Modifier,
    title: String,
    initialSelected: List<T> = emptyList(),
    onSelectionChanged: (List<T>) -> Unit = {},
    label: (T) -> String,
) {
    var isExpended by remember { mutableStateOf(false) }
    var selectedItems by remember { mutableStateOf(initialSelected) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpended = !isExpended }
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(Dimens.CardElevation)
    ) {
        Column(modifier = Modifier.padding(Dimens.DefaultPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    val selectedItemText = if (selectedItems.isNotEmpty())
                        "${selectedItems.size} selected" else "None"
                    Text(
                        text = selectedItemText,
                        style = MaterialTheme.typography.bodyMedium,
                    )

                }
                Icon(
                    imageVector = if (isExpended) Default.ExpandLess else Default.ExpandMore,
                    contentDescription = if (isExpended) "Fold" else "Expand"
                )
            }

            if (isExpended) {
                Spacer(modifier = Modifier.height(Dimens.DefaultPadding))
                FlowColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
                ) {
                    items.forEach { item ->
                        val isSelected = item in selectedItems
                        ElevatedFilterChip(
                            shape = MaterialTheme.shapes.medium,
                            selected = isSelected,
                            label = { Text(label(item)) },
                            leadingIcon = {
                                if (isSelected) Icon(
                                    Default.Check,
                                    contentDescription = null
                                ) else Spacer(Modifier.width(24.dp))
                            },
                            onClick = {
                                selectedItems = if (isSelected) {
                                    selectedItems - item
                                } else {
                                    selectedItems + item
                                }
                                onSelectionChanged(selectedItems)
                            }
                        )
                    }
                }

            }
        }
    }
}
