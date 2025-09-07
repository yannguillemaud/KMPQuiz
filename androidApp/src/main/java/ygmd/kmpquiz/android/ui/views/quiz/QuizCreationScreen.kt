package ygmd.kmpquiz.android.ui.views.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.domain.entities.cron.CronPreset
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.viewModel.error.UiEvent
import ygmd.kmpquiz.viewModel.quiz.QuizIntent
import ygmd.kmpquiz.viewModel.quiz.QuizViewModel
import ygmd.kmpquiz.viewModel.settings.UiCronSetting

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuizCreationScreen(
    onSavedQuiz: () -> Unit,
    onCancelCreation: () -> Unit,
    quizViewModel: QuizViewModel = koinViewModel(),
) {
    val availableCategories = quizViewModel.qandas.collectAsState(emptyList())

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedQandas by remember { mutableStateOf(listOf<Qanda>()) }
    var selectedCron: UiCronSetting? by remember { mutableStateOf(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    fun tryQuizCreation() {
        quizViewModel.processIntent(
            QuizIntent.CreateQuiz(
                title = title,
                qandas = selectedQandas,
                cronSetting = selectedCron
            )
        )
        onSavedQuiz()
    }

    LaunchedEffect(Unit) {
        quizViewModel.quizEvents.collect { event ->
            val result = when (event) {
                is UiEvent.Success -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.Error -> snackbarHostState.showSnackbar(
                    message = event.message,
                    actionLabel = event.action?.label,
                )
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
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
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titre du quiz") },
                modifier = Modifier.fillMaxWidth(),
                isError = title.isBlank() && title.isNotEmpty()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            SelectableCategories(
                questionsByCategories = availableCategories.value
                    .groupBy { it.metadata.category },
                onSelectQandas = { selectedQandas = it }
            )
            SelectableCron(
                onSelectCron = { selectedCron = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    tryQuizCreation()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enregistrer")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectableCategories(
    questionsByCategories: Map<String, List<Qanda>>,
    onSelectQandas: (List<Qanda>) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }

    // Texte affiché dans le champ (résumé des sélections)
    val displayText =
        if (selectedCategories.isEmpty()) "Aucune sélection"
        else selectedCategories.joinToString()

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it }
    ) {
        OutlinedTextField(
            value = displayText,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            label = { Text("Catégories") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = colorScheme.outline.copy(alpha = 0.38f),
                disabledLabelColor = colorScheme.onSurface.copy(alpha = 0.38f),
                disabledTextColor = colorScheme.onSurface.copy(alpha = 0.38f)
            )
        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            // 👇 on laisse le menu ouvert pour multi-select
            onDismissRequest = { isExpanded = false },
            modifier = Modifier.exposedDropdownSize()
        ) {
            Column(Modifier.padding(8.dp)) {
                questionsByCategories.forEach { (categoryName, qandas) ->
                    val isSelected = categoryName in selectedCategories
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedCategories =
                                if (isSelected) selectedCategories - categoryName
                                else selectedCategories + categoryName
                            onSelectQandas(qandas)
                        },
                        label = { Text(categoryName) },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
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
                        onSelectCron(
                            UiCronSetting(
                                title = it.displayName,
                                cronExpression = it.toCronExpression(),
                            )
                        )
                    }
                )
            }
        }
    }
}