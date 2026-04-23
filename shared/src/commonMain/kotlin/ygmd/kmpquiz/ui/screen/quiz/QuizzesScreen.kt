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
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.domain.viewModel.quiz.QuizViewModel
import ygmd.kmpquiz.domain.viewModel.quiz.QuizzesUiState
import ygmd.kmpquiz.events.event.Event
import ygmd.kmpquiz.ui.composable.createquiz.LoadingState
import ygmd.kmpquiz.ui.composable.playquiz.ErrorState
import ygmd.kmpquiz.ui.composable.playquiz.QuizCard
import ygmd.kmpquiz.ui.theme.Dimens.DefaultPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizzesScreen(
    quizViewModel: QuizViewModel = koinViewModel(),
    onNavigateToQuizCreation: () -> Unit = {},
    onNavigateToQuizSettings: (quizId: String) -> Unit = {},
    onNavigateToPlayQuiz: (quizId: String) -> Unit = {},
) {
    val quizzesState = quizViewModel.quizzesState.collectAsState(QuizzesUiState(isLoading = true))
    val snackbarhostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        quizViewModel.quizEvents.collect { event ->
            if (event is Event.SnackbarEvent) {
                snackbarhostState.showSnackbar(
                    message = event.message,
                    duration = SnackbarDuration.Short
                )
            }
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
            val quizzesUiState = quizzesState.value
            when {
                quizzesUiState.isLoading -> LoadingState(modifier = Modifier.fillMaxSize())
                quizzesUiState.error != null -> ErrorState(
                    modifier = Modifier.fillMaxSize(),
                    message = quizzesUiState.error.message,
                )

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(DefaultPadding),
                        verticalArrangement = Arrangement.spacedBy(DefaultPadding),
                    ) {
                        items(
                            items = quizzesUiState.quizzes,
                            key = { it.id }
                        ) { quiz ->
                            QuizCard(
                                modifier = Modifier.fillMaxWidth(),
                                quiz = quiz,
                                isEnabled = quiz.questionsSize > 0,
                                onClick = { onNavigateToPlayQuiz(quiz.id) },
                                onEdit = { onNavigateToQuizSettings(quiz.id) },
                                onDelete = { quizViewModel.deleteQuiz(quiz.id) },
                                onToggleCron = { newValue ->
                                    quiz.cron?.let { quizCron ->
                                        quizViewModel.toggleCron(
                                            quizId = quiz.id,
                                            cronId = quizCron.id,
                                            isEnabled = newValue
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}