package ygmd.kmpquiz.presentation.composable.createquiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ygmd.kmpquiz.presentation.composable.EmptyState
import ygmd.kmpquiz.presentation.viewModel.displayable.DisplayableCategory
import ygmd.kmpquiz.presentation.viewModel.displayable.DisplayableCategoryWithCount

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategorySelectionSection(
    categories: List<DisplayableCategory>,
    selectedCategories: List<DisplayableCategoryWithCount>,
    onCategoryToggled: (String) -> Unit,
) {
    if (categories.isEmpty()) {
        EmptyState(
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Default.Category,
            title = "No Categories Found",
            description = "Please download qandas in home page to start creating quizzes."
        )
    } else {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                val isSelected = selectedCategories.any { it.id == category.id }
                ElevatedFilterChip(
                    selected = isSelected,
                    onClick = { onCategoryToggled(category.id) },
                    label = { Text(category.name) },
                    leadingIcon = if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null,
                    colors = FilterChipDefaults.elevatedFilterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

