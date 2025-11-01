package ygmd.kmpquiz.ui.screen.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.domain.viewModel.error.UiEvent
import ygmd.kmpquiz.domain.viewModel.quiz.QuizViewModel
import ygmd.kmpquiz.domain.viewModel.quiz.QuizzesIntent
import ygmd.kmpquiz.domain.viewModel.quiz.edit.QuizEditViewModel
import ygmd.kmpquiz.domain.viewModel.state.UiState
import ygmd.kmpquiz.ui.composable.createquiz.LoadingState
import ygmd.kmpquiz.ui.composable.playquiz.ErrorState
import ygmd.kmpquiz.ui.composable.playquiz.QuizCard
import ygmd.kmpquiz.ui.theme.Dimens.DefaultPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizzesScreen(
    quizViewModel: QuizViewModel = koinViewModel(),
    quizEditViewModel: QuizEditViewModel = koinViewModel(),
    onNavigateToQuizCreation: () -> Unit = {},
    onNavigateToQuizSettings: (quizId: String) -> Unit = {},
    onNavigateToPlayQuiz: (quizId: String) -> Unit = {},
) {
    val quizzesState = quizViewModel.quizzesState.collectAsState(UiState.Loading)
    val snackbarhostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit){
        quizViewModel.quizEvents.collectLatest { event ->
            if (event is UiEvent.Success) snackbarhostState.showSnackbar(
                message = event.message,
                duration = SnackbarDuration.Short
            )
        }

        quizEditViewModel.events.collectLatest { event ->
            if (event is UiEvent.Success)
                snackbarhostState.showSnackbar(
                    message = event.message,
                )
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Quizzes", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                actions = {
                    IconButton(onClick = onNavigateToQuizCreation) {
                        Icon(Icons.Outlined.Add, contentDescription = "Create new quiz")
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarhostState)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (val state = quizzesState.value) {
                is UiState.Loading -> LoadingState(modifier = Modifier.fillMaxSize())
                is UiState.Error -> ErrorState(
                    modifier = Modifier.fillMaxSize(),
                    message = state.error.message,
                )

                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(DefaultPadding),
                        verticalArrangement = Arrangement.spacedBy(DefaultPadding),
                    ) {
                        items(
                            items = state.data,
                            key = { it.id }
                        ) { quiz ->
                            QuizCard(
                                modifier = Modifier.fillMaxWidth(),
                                quiz = quiz,
                                isEnabled = quiz.questionsSize > 0,
                                onClick = { onNavigateToPlayQuiz(quiz.id) },
                                onEdit = { onNavigateToQuizSettings(quiz.id) },
                                onDelete = {
                                    quizViewModel.processIntent(
                                        QuizzesIntent.DeleteQuiz(quiz.id)
                                    )
                                },
                                onToggleCron = {
                                    quizViewModel.processIntent(
                                        QuizzesIntent.ToggleCron(quiz.id, isEnabled = it)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}