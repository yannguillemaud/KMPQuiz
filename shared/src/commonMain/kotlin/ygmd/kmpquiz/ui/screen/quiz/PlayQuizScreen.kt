package ygmd.kmpquiz.ui.screen.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableQuizSession
import ygmd.kmpquiz.domain.viewModel.quiz.session.QuizSessionIntent
import ygmd.kmpquiz.domain.viewModel.quiz.session.QuizSessionViewModel
import ygmd.kmpquiz.domain.viewModel.state.UiState
import ygmd.kmpquiz.ui.composable.createquiz.LoadingState
import ygmd.kmpquiz.ui.composable.playquiz.ErrorState
import ygmd.kmpquiz.ui.composable.playquiz.NextQuestionButton
import ygmd.kmpquiz.ui.composable.playquiz.QuizInProgressDisplay
import ygmd.kmpquiz.ui.composable.playquiz.QuizResultDisplay
import ygmd.kmpquiz.ui.theme.Dimens.DefaultPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayQuizScreen(
    quizId: String,
    onFinished: () -> Unit,
    quizSessionViewModel: QuizSessionViewModel = koinViewModel(parameters = { parametersOf(quizId) }),
) {
    val quizUiState by quizSessionViewModel.uiState.collectAsState(UiState.Loading)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    when (val state = quizUiState) {
                        is UiState.Loading -> LoadingState()
                        is UiState.Error -> ErrorState(message = "Error loading quiz")
                        is UiState.Success<DisplayableQuizSession> -> when (val quiz = state.data) {
                            is DisplayableQuizSession.InProgress -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = quiz.title,
                                    style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, fontSize = 22.sp
                                )
                                Text(
                                    text = "${quiz.index + 1} / ${quiz.size}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }

                            is DisplayableQuizSession.Completed -> {
                                Text(
                                    text = quiz.title,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            when (val state = quizUiState) {
                is UiState.Success<DisplayableQuizSession> ->
                    if (state.data is DisplayableQuizSession.InProgress && state.data.hasAnswered) {
                        NextQuestionButton(onClick = {
                            quizSessionViewModel.processIntent(QuizSessionIntent.NextState)
                        })
                    }

                else -> {}
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = quizUiState) {
                is UiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        LoadingState()
                    }
                }

                is UiState.Error ->
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        ErrorState(message = "Error loading quiz")
                    }

                is UiState.Success<DisplayableQuizSession> -> when (val quiz = state.data) {
                    is DisplayableQuizSession.InProgress -> QuizInProgressDisplay(
                        progressState = quiz,
                        onSelectAnswer = { choice ->
                            quizSessionViewModel.processIntent(QuizSessionIntent.SelectAnswer(choice))
                        }
                    )

                    is DisplayableQuizSession.Completed ->
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            QuizResultDisplay(
                                modifier = Modifier.padding(DefaultPadding),
                                results = quiz.results,
                                onFinished = onFinished
                            )
                        }
                }
            }
        }
    }
}








