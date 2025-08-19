package ygmd.kmpquiz.android.ui.views.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.domain.entities.cron.CronPreset
import ygmd.kmpquiz.viewModel.quiz.QuizIntent
import ygmd.kmpquiz.viewModel.quiz.QuizViewModel
import ygmd.kmpquiz.viewModel.settings.UiCronSetting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizCreationScreen(
    onSavedQuiz: () -> Unit,
    onCancelCreation: () -> Unit,
    quizViewModel: QuizViewModel = koinViewModel(),
) {
    val identifiers = quizViewModel.qandas.collectAsState(emptyList())

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedIdentifier by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var selectedCron: UiCronSetting? by remember { mutableStateOf(null) }

    fun createQuiz() {
        quizViewModel.processIntent(
            QuizIntent.CreateQuiz(
                title,
                selectedIdentifier,
                cronSetting = selectedCron
            ))
    }

    fun validateAndSave() {
        if (title.isBlank()) {
            showErrorDialog = true
        } else {
            createQuiz()
            onSavedQuiz()
        }
    }

    // Dialog d'erreur
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = {
                Text("Erreur")
            },
            text = {
                Text("Quiz title cannot be blank")
            },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Créer un quiz") },
                navigationIcon = {
                    IconButton(onClick = onCancelCreation) {
                        Icon(Icons.Default.Close, contentDescription = "Annuler")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titre du quiz") },
                modifier = Modifier.fillMaxWidth(),
                isError = title.isBlank() && title.isNotEmpty()
            )
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            SelectableCategory(
                questionsByCategory = identifiers.value
                    .groupBy { it.metadata.category }
                    .mapValues { it.value.count() },
                onSelectIdentifier = { selectedIdentifier = it }
            )
            SelectableCron(
                onSelectCron = { selectedCron = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { validateAndSave() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enregistrer")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectableCategory(
    questionsByCategory: Map<String, Int>,
    onSelectIdentifier: (String) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    var identifierName by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it }
    ) {
        OutlinedTextField(
            value = identifierName,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            label = { Text("Catégorie") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = colorScheme.outline.copy(alpha = 0.38f),
                disabledLabelColor = colorScheme.onSurface.copy(alpha = 0.38f),
                disabledTextColor = colorScheme.onSurface.copy(alpha = 0.38f)
            )
        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier.exposedDropdownSize()
        ) {
            questionsByCategory.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${it.key}: ${it.value} qandas",
                            style = typography.bodyMedium
                        )
                    },
                    onClick = {
                        isExpanded = false
                        identifierName = it.key
                        onSelectIdentifier(it.key)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectableCron(
    onSelectCron: (UiCronSetting) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    var cronDisplayName by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it }
    ) {
        OutlinedTextField(
            value = cronDisplayName,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            label = { Text("Catégorie") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = colorScheme.outline.copy(alpha = 0.38f),
                disabledLabelColor = colorScheme.onSurface.copy(alpha = 0.38f),
                disabledTextColor = colorScheme.onSurface.copy(alpha = 0.38f)
            )
        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier.exposedDropdownSize()
        ) {
            CronPreset.entries.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = it.displayName,
                            style = typography.bodyMedium
                        )
                    },
                    onClick = {
                        isExpanded = false
                        cronDisplayName = it.displayName
                        onSelectCron(UiCronSetting(
                            title = it.displayName,
                            cronExpression = it.toCronExpression(),
                        ))
                    }
                )
            }
        }
    }
}