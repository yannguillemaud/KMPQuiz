package ygmd.kmpquiz.android.ui.views.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.android.ui.composable.quiz.QuizCompletedSection
import ygmd.kmpquiz.android.ui.composable.quiz.QuizErrorSection
import ygmd.kmpquiz.android.ui.composable.quiz.QuizInProgressSection
import ygmd.kmpquiz.android.ui.composable.quiz.QuizLoadingSection
import ygmd.kmpquiz.viewModel.quiz.session.QuizSessionIntent
import ygmd.kmpquiz.viewModel.quiz.session.QuizSessionUiState
import ygmd.kmpquiz.viewModel.quiz.session.QuizSessionViewModel

@Composable
fun QuizSessionScreen(
    quizId: String,
    onNavigateHome: () -> Unit,
    onSessionFinished: () -> Unit,
    viewModel: QuizSessionViewModel = koinViewModel()
) {
    val uiState by viewModel.quizUiState.collectAsState()

    LaunchedEffect(quizId) {
        viewModel.processIntent(QuizSessionIntent.StartQuizSession(quizId))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .statusBarsPadding()
    ) {
        when (val state = uiState) {
            is QuizSessionUiState.Idle -> QuizLoadingSection()

            is QuizSessionUiState.InProgress -> QuizInProgressSection(
                quiz = state,
                onAnswerSelected = { viewModel.processIntent(QuizSessionIntent.SelectAnswer(it)) },
                onNextQuestion = { viewModel.processIntent(QuizSessionIntent.NextQuestion) },
                onNavigateBack = onSessionFinished,
            )

            is QuizSessionUiState.Completed -> QuizCompletedSection(
                state = state,
                onNavigateBack = onNavigateHome
            )

            is QuizSessionUiState.Error -> QuizErrorSection(
                message = state.message,
                onNavigateBack = onNavigateHome
            )
        }
    }
}
