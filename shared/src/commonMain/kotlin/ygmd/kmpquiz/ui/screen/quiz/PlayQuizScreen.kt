package ygmd.kmpquiz.ui.screen.quiz

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableQuizSession
import ygmd.kmpquiz.domain.viewModel.quiz.session.QuizSessionIntent
import ygmd.kmpquiz.domain.viewModel.quiz.session.QuizSessionViewModel
import ygmd.kmpquiz.domain.viewModel.state.UiState
import ygmd.kmpquiz.ui.composable.createquiz.LoadingState
import ygmd.kmpquiz.ui.composable.playquiz.ErrorState
import ygmd.kmpquiz.ui.composable.playquiz.QandaQuizCard
import ygmd.kmpquiz.ui.composable.playquiz.QuizResultDisplay

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
                    val state = quizUiState
                    if (state is UiState.Success && state.data is DisplayableQuizSession.InProgress) {
                        val session = state.data.session
                        QuizProgressIndicator(
                            currentStep = session.currentIndex + 1,
                            totalSteps = session.quiz.qandas.size
                        )
                    } else {
                        Text("Quiz")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            if (quizUiState is UiState.Success) {
                val data = (quizUiState as UiState.Success).data
                if (data is DisplayableQuizSession.InProgress) {
                    val isNextEnabled = data.hasAnswered
                    Row(
                        modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
                        horizontalArrangement = Arrangement.End
                    )
                    {
                        SmallFloatingActionButton(
                            containerColor = if (isNextEnabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
                            shape = FloatingActionButtonDefaults.smallShape,
                            onClick = {
                                if (isNextEnabled) quizSessionViewModel.processIntent(
                                    QuizSessionIntent.NextState
                                )
                            },
                            modifier = Modifier
                                .navigationBarsPadding()
                                .alpha(if (isNextEnabled) 1f else 0f)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward
                                null
                            )
                        }
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.EndOverlay
    ) { paddingValues ->
        Surface(
            modifier = Modifier.fillMaxSize().padding(paddingValues).animateContentSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            when (val state = quizUiState) {
                is UiState.Loading -> LoadingState()
                is UiState.Error -> ErrorState(message = "Error occured")
                is UiState.Success -> {
                    when (val quiz = state.data) {
                        is DisplayableQuizSession.InProgress -> {
                            QandaQuizCard(
                                qanda = quiz.currentQanda,
                                selectedAnswer = quiz.selectedAnswer,
                                onSelectAnswer = { choice ->
                                    if (!quiz.hasAnswered) {
                                        quizSessionViewModel.processIntent(
                                            QuizSessionIntent.SelectAnswer(
                                                choice
                                            )
                                        )
                                    }
                                }
                            )
                        }

                        is DisplayableQuizSession.Completed -> {
                            QuizResultDisplay(results = quiz.results, onFinished = onFinished)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizProgressIndicator(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Question $currentStep / $totalSteps",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { currentStep.toFloat() / totalSteps },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(6.dp)
                .clip(CircleShape),
            strokeCap = StrokeCap.Round
        )
    }
}