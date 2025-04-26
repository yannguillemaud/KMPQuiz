package ygmd.kmpquiz.ui.composable.createquiz

import MultiSelectCard
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ygmd.kmpquiz.domain.model.cron.CronExpression
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableCategory
import ygmd.kmpquiz.ui.theme.Dimens.DefaultPadding

@Composable
fun QuizSettingsForm(
    modifier: Modifier = Modifier,
    title: String?,
    onTitleChange: (String) -> Unit,
    titleError: String? = null,
    selectedCategories: List<DisplayableCategory> = emptyList(),
    availableCategories: List<DisplayableCategory> = emptyList(),
    onCategoryChange: (List<DisplayableCategory>) -> Unit,
    selectedCron: CronExpression? = null,
    availableCrons: List<CronExpression> = emptyList(),
    onCronChange: (CronExpression?) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(DefaultPadding)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = title ?: "",
            onValueChange = onTitleChange,
            label = { Text("Quiz Title") },
            singleLine = true,
            placeholder = { Text("Enter a quiz title") },
            isError = titleError != null,
            supportingText = {
                if (titleError != null) {
                    Text(titleError)
                }
            },
            trailingIcon = {
                if (titleError != null) {
                    Icon(
                        Icons.Default.ErrorOutline,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        MultiSelectCard(
            modifier = Modifier.fillMaxWidth(),
            title = "Categories",
            items = availableCategories,
            initialSelected = selectedCategories,
            onSelectionChanged = onCategoryChange,
            label = { it.name },
        )

        SelectCard(
            modifier = Modifier.fillMaxWidth(),
            items = availableCrons,
            title = "Reminder",
            initialSelected = selectedCron,
            onSelectionChanged = { onCronChange(it) },
            itemLabel = { it.displayName },
        )
    }
}
