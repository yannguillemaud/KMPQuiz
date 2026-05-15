package ygmd.kmpquiz.ui.screen.quiz

import CategorySelectionSection
import QuizConfigSection
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.brewkits.grant.compose.GrantDialog
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.domain.viewModel.quiz.edit.QuizEditViewModel
import ygmd.kmpquiz.events.event.Event
import ygmd.kmpquiz.ui.composable.createquiz.SchedulerSection

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        letterSpacing = 1.sp
    )
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
    LaunchedEffect(Unit){
        viewModel.onAppResumed()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (isEditMode) "Edit Quiz" else "Create Quiz") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
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
                    if (quiz.metadata.isLoading) CircularProgressIndicator()
                    else Icon(Icons.Default.Save, contentDescription = null)
                },
                text = {
                    Text(if (quiz.metadata.isLoading) "Loading" else "Save Quiz")
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (quiz.metadata.isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                /* TITLE */
                item(key = "title_field") {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        value = quiz.content.title,
                        onValueChange = viewModel::updateTitle,
                        label = { Text("Quiz Title", fontWeight = FontWeight.Bold) },
                        isError = quiz.content.titleError != null,
                        supportingText = { quiz.content.titleError?.let { Text(it) } },
                        shape = MaterialTheme.shapes.medium
                    )
                }

                /* SCHEDULER */
                item(key = "cron_section") {
                    SectionHeader(title = "Scheduler")
                    GrantDialog(handler = viewModel.notificationPermission)
                    SchedulerSection(
                        currentSelection = quiz.configuration.selectedScheduler.selectedScheduler,
                        isEnabled = quiz.configuration.selectedScheduler.isEnabled,
                        onSelectionChange = viewModel::setScheduler,
                        onToggleEnabled = viewModel::toggleSelectScheduler
                    )
                }

                /* CATEGORIES */
                item(key = "categories_section") {
                    SectionHeader(title = "Categories")
                    CategorySelectionSection(
                        categories = quiz.content.availableCategories,
                        selectedCategories = quiz.configuration.selectedCategories,
                        onCategoryToggled = viewModel::toggleSelectCategory
                    )
                }

                /* CONFIG */
                item(key = "config_section") {
                    SectionHeader(title = "Categories Settings")
                    QuizConfigSection(
                        quizMode = quiz.configuration.selectedQuizMode,
                        onModeSelected = viewModel::switchConfigMode,
                        onGlobalLimitChanged = viewModel::updateGlobalLimit,
                        onCategoryLimitChanged = viewModel::updateCategoryLimit
                    )
                }

                item(key = "questions_count") {
                    Text("Quiz will contain ${quiz.configuration.totalAvailableQuestions} questions")
                }
            }
        }
    }
}