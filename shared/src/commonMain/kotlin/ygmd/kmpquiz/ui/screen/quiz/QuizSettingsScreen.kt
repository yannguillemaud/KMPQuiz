package ygmd.kmpquiz.ui.screen.quiz

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.domain.viewModel.category.CategoryViewModel
import ygmd.kmpquiz.domain.viewModel.error.UiEvent
import ygmd.kmpquiz.domain.viewModel.quiz.edit.QuizEditIntent
import ygmd.kmpquiz.domain.viewModel.quiz.edit.QuizEditUiState
import ygmd.kmpquiz.domain.viewModel.quiz.edit.QuizEditViewModel
import ygmd.kmpquiz.domain.viewModel.state.UiState
import ygmd.kmpquiz.domain.viewModel.state.getOrDefault
import ygmd.kmpquiz.ui.composable.createquiz.LoadingState
import ygmd.kmpquiz.ui.composable.createquiz.QuizSettingsForm
import ygmd.kmpquiz.ui.composable.playquiz.ErrorState
import ygmd.kmpquiz.ui.theme.Dimens.DefaultPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSettingsScreen(
    quizId: String,
    onNavigateBack: () -> Unit = {},
    onFinished: () -> Unit = {},
    quizEditViewModel: QuizEditViewModel = koinViewModel(),
    categoryViewModel: CategoryViewModel = koinViewModel(),
) {
    val editUiState: UiState<QuizEditUiState> by quizEditViewModel.quizEditUiState.collectAsState()
    val availableCategories = categoryViewModel.categories.collectAsState()
    val availableCrons = quizEditViewModel.availableCrons.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit){
        quizEditViewModel.processIntent(QuizEditIntent.Load(quizId))
        quizEditViewModel.events.collectLatest {
            if (it is UiEvent.Error) snackbarHostState.showSnackbar(it.error.message)
            else onFinished()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Quiz Settings", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (editUiState is UiState.Success) {
                        quizEditViewModel.processIntent(QuizEditIntent.Save)
                    }
                }
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save Quiz")
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(DefaultPadding)) {
            when (val state = editUiState) {
                is UiState.Loading -> LoadingState()
                is UiState.Error -> ErrorState(message = state.error.message)
                is UiState.Success -> {
                    val quiz = state.data
                    key(quiz.categories, quiz.cron) {
                        QuizSettingsForm(
                            title = quiz.title,
                            onTitleChange = {
                                quizEditViewModel.processIntent(QuizEditIntent.UpdateTitle(it))
                            },
                            titleError = quiz.titleError,
                            selectedCategories = quiz.categories,
                            availableCategories = availableCategories.value.getOrDefault(emptyList()),
                            onCategoryChange = {
                                quizEditViewModel.processIntent(
                                    QuizEditIntent.UpdateCategories(it.toList())
                                )
                            },
                            selectedCron = quiz.cron,
                            availableCrons = availableCrons.value,
                            onCronChange = {
                                quizEditViewModel.processIntent(QuizEditIntent.UpdateCron(it))
                            },
                        )
                    }
                }
            }
        }
    }
}
