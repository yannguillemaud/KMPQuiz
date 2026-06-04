package ygmd.kmpquiz.ui.screen.quiz

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.brewkits.grant.compose.GrantDialog
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.domain.viewModel.quiz.edit.QuizEditViewModel
import ygmd.kmpquiz.events.event.Event
import ygmd.kmpquiz.ui.composable.createquiz.CategorySelectionSection
import ygmd.kmpquiz.ui.composable.createquiz.QuizQuestionConfigSection
import ygmd.kmpquiz.ui.composable.createquiz.SchedulerConfigurationSection

@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(
        modifier = Modifier.padding(bottom = 12.dp, top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizEditorScreen(
    isEditMode: Boolean,
    quizId: String? = null,
    viewModel: QuizEditViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val quiz by viewModel.quizState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // setup quiz if editing
    LaunchedEffect(isEditMode) {
        if (isEditMode && quizId != null) viewModel.setUp(quizId)
    }

    // listen events and show snackbar
    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            if (event is Event.NavBackEvent) onNavigateBack()
            else if (event is Event.SnackbarEvent) snackbarHostState.showSnackbar(
                message = event.message,
                withDismissAction = true,
                duration = Short
            )
        }
    }

    // permissions refresh
    LaunchedEffect(Unit) {
        viewModel.onAppResumed()
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
            ExtendedFloatingActionButton(
                onClick = {
                    val canSave = !quiz.metadata.isSaving && !quiz.metadata.isLoading
                    if (canSave) viewModel.saveQuiz()
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                icon = {
                    if (quiz.metadata.isSaving) CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
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
                        modifier = Modifier.padding(top = 16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                /* TITLE */
                item(key = "title_field") {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
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
                        Column(modifier = Modifier.padding(16.dp)) {
                            SectionHeader(title = "Scheduler", icon = Icons.Default.Schedule)
                            GrantDialog(handler = viewModel.notificationPermission)
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
                        Column(modifier = Modifier.padding(16.dp)) {
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
                        Column(modifier = Modifier.padding(16.dp)) {
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
                        modifier = Modifier.fillMaxWidth().padding(bottom = 80.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (quiz.configuration.noQuestionsSelectedError != null)
                                MaterialTheme.colorScheme.errorContainer
                            else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
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