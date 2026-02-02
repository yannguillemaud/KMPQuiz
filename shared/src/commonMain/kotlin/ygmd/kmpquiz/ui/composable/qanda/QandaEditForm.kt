package ygmd.kmpquiz.ui.composable.qanda

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableCategory
import ygmd.kmpquiz.domain.viewModel.qandas.edit.TextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QandaEditForm(
    modifier: Modifier = Modifier,
    question: TextField,
    selectedCategory: DisplayableCategory?,
    availableCategories: List<DisplayableCategory>,
    onCategorySelected: (DisplayableCategory) -> Unit,
    correctAnswer: TextField,
    incorrectAnswers: Map<Int, TextField>,
    canAddIncorrectAnswer: Boolean,
    onUpdateQuestion: (String) -> Unit,
    onAddNewIncorrectAnswer: () -> Unit,
    onRemoveIncorrectAnswer: (Int) -> Unit,
    onUpdateIncorrectAnswer: (Int, String) -> Unit,
    onUpdateCorrectAnswer: (String) -> Unit
) {
    val scroll = rememberScrollState()
    var isCategoryMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scroll)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // --------------------------
        // CATEGORY DROPDOWN
        // --------------------------
        ExposedDropdownMenuBox(
            expanded = isCategoryMenuExpanded,
            onExpandedChange = { isCategoryMenuExpanded = !isCategoryMenuExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                    .fillMaxWidth(),
                value = selectedCategory?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Catégorie") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryMenuExpanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )

            ExposedDropdownMenu(
                expanded = isCategoryMenuExpanded,
                onDismissRequest = { isCategoryMenuExpanded = false }
            ) {
                availableCategories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            onCategorySelected(category)
                            isCategoryMenuExpanded = false
                        }
                    )
                }
            }
        }

        // --------------------------
        // QUESTION
        // --------------------------
        OutlinedTextField(
            value = question.value,
            onValueChange = onUpdateQuestion,
            label = { Text("Question") },
            isError = question.error != null,
            supportingText = {
                question.error?.let { Text(it) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // --------------------------
        // CORRECT ANSWER
        // --------------------------
        OutlinedTextField(
            value = correctAnswer.value,
            onValueChange = onUpdateCorrectAnswer,
            label = { Text("Bonne réponse") },
            isError = correctAnswer.error != null,
            supportingText = {
                correctAnswer.error?.let { Text(it) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // --------------------------
        // INCORRECT ANSWERS
        // --------------------------
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            incorrectAnswers.entries.forEach { (index, entry) ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = entry.value,
                        onValueChange = { onUpdateIncorrectAnswer(index, it) },
                        label = { Text("Mauvaise réponse") },
                        isError = entry.error != null,
                        supportingText = {
                            entry.error?.let { Text(it) }
                        }
                    )

                    IconButton(
                        onClick = { onRemoveIncorrectAnswer(index) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Remove,
                            contentDescription = "Supprimer"
                        )
                    }
                }
            }

            if (canAddIncorrectAnswer) {
                FilledTonalButton(
                    onClick = onAddNewIncorrectAnswer,
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Icon(
                        Icons.Outlined.Add,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Ajouter une réponse")
                }
            }
        }
    }
}
