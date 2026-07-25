package ygmd.kmpquiz.presentation.screen.quiz

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Short
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.core.domain.event.Event
import ygmd.kmpquiz.presentation.composable.SectionHeader
import ygmd.kmpquiz.core.service.permission.PermissionRationaleDialog
import ygmd.kmpquiz.presentation.composable.createquiz.CategorySelectionSection
import ygmd.kmpquiz.presentation.composable.createquiz.QuizQuestionConfigSection
import ygmd.kmpquiz.presentation.composable.createquiz.SchedulerConfigurationSection
import ygmd.kmpquiz.presentation.theme.Dimens
import ygmd.kmpquiz.presentation.viewModel.quiz.edit.QuizEditViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizEditorScreen(
    isEditMode: Boolean,
    quizId: String? = null,
    viewModel: QuizEditViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {},
) {
    val quiz by viewModel.quizState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // setup quiz if editing mode
    LaunchedEffect(isEditMode) {
        if (isEditMode && quizId != null) viewModel.setUp(quizId)
    }

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            if (event is Event.NavigateBack) onNavigateBack()
            else if (event is Event.ShowSnackbar) snackbarHostState.showSnackbar(
                message = event.message,
                withDismissAction = true,
                duration = Short
            )
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Quiz" else "Create Quiz",
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            val canSave = !quiz.metadata.isSaving && !quiz.metadata.isLoading
            ExtendedFloatingActionButton(
                onClick = { if (canSave) viewModel.saveQuiz() },
                containerColor = if(canSave) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                icon = {
                    if (quiz.metadata.isSaving) CircularProgressIndicator(
                        modifier = Modifier.size(Dimens.IconSize),
                        strokeWidth = Dimens.ThinStrokeWidth,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    else Icon(Icons.Default.Save, contentDescription = null)
                },
                text = {
                    Text(if (quiz.metadata.isSaving) "Saving..." else "Save Quiz")
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        PermissionRationaleDialog(handler = viewModel.notificationPermission)
        if (quiz.metadata.isLoading) {
            Surface(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Text(
                        "Loading quiz data...",
                        modifier = Modifier.padding(top = Dimens.PaddingMedium),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = Dimens.PaddingMedium),
                verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
            ) {
                /* TITLE */
                item(key = "title_field") {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = Dimens.PaddingSmall),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(Dimens.PaddingMedium)) {
                            SectionHeader(title = "General Info", icon = Icons.Default.Title)
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth().clearFocusOnTap(),
                                value = quiz.content.title,
                                onValueChange = viewModel::updateTitle,
                                label = { Text("Quiz Title") },
                                isError = quiz.content.titleError != null,
                                supportingText = { quiz.content.titleError?.let { Text(it) } },
                                shape = MaterialTheme.shapes.medium,
                                singleLine = true,
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Title,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                    )
                                }
                            )
                        }
                    }
                }

                /* SCHEDULER */
                item(key = "cron_section") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(Dimens.PaddingMedium)) {
                            SectionHeader(title = "Scheduler", icon = Icons.Default.Schedule)
                            SchedulerConfigurationSection(
                                currentSelection = quiz.configuration.selectedScheduler.selectedScheduler,
                                isEnabled = quiz.configuration.selectedScheduler.isEnabled,
                                onSelectionChange = viewModel::setScheduler,
                                onToggleEnabled = viewModel::toggleSelectScheduler
                            )
                        }
                    }
                }

                /* CATEGORIES */
                item(key = "categories_section") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(Dimens.PaddingMedium)) {
                            SectionHeader(title = "Categories", icon = Icons.Default.Category)
                            CategorySelectionSection(
                                categories = quiz.content.availableCategories,
                                selectedCategories = quiz.configuration.selectedCategories,
                                onCategoryToggled = viewModel::toggleSelectCategory
                            )
                        }
                    }
                }

                /* CONFIG */
                item(key = "config_section") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(Dimens.PaddingMedium)) {
                            SectionHeader(title = "Questions Settings", icon = Icons.Default.Settings)
                            QuizQuestionConfigSection(
                                quizMode = quiz.configuration.selectedQuizMode,
                                onModeSelected = viewModel::switchConfigMode,
                                onGlobalLimitChanged = viewModel::updateGlobalLimit,
                                onCategoryLimitChanged = viewModel::updateCategoryLimit
                            )
                        }
                    }
                }

                item(key = "questions_count") {
                    Card(
                        // Reserve space so the last card clears the floating nav capsule.
                        modifier = Modifier.fillMaxWidth().padding(bottom = Dimens.BottomNavPadding),
                        colors = CardDefaults.cardColors(
                            containerColor = if (quiz.configuration.noQuestionsSelectedError != null)
                                MaterialTheme.colorScheme.errorContainer
                            else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(Dimens.PaddingMedium),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMediumSmall)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = if (quiz.configuration.noQuestionsSelectedError != null)
                                    MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.primary
                            )
                            val error = quiz.configuration.noQuestionsSelectedError
                            if (error != null) {
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    text = "This quiz will contain ${quiz.configuration.totalAvailableQuestions} questions.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/* Helper to clear focus for title text field */
private fun Modifier.clearFocusOnTap(): Modifier = composed {
    val focusManager = LocalFocusManager.current
    Modifier.pointerInput(Unit) {
        detectTapGestures(onTap = {
            focusManager.clearFocus()
        })
    }
}