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
import ygmd.kmpquiz.viewModel.quiz.QuizUiState
import ygmd.kmpquiz.viewModel.quiz.QuizViewModel

@Composable
fun QuizScreen(
    qandaIds: List<Long>,
    onNavigateBack: () -> Unit,
    viewModel: QuizViewModel = koinViewModel()
) {
    val uiState by viewModel.quizUiState.collectAsState()

    LaunchedEffect(qandaIds) {
        viewModel.startQuiz(qandaIds)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .statusBarsPadding()
    ) {
        when(val state = uiState){
            is QuizUiState.Idle -> QuizLoadingSection()
            is QuizUiState.InProgress -> QuizInProgressSection(
                state = state,
                onAnswerSelected = viewModel::selectAnswer,
                onNextQuestion = viewModel::goToNextQuestion,
                onNavigateBack = onNavigateBack,
            )
            is QuizUiState.Completed -> QuizCompletedSection(
                state = state,
                onNavigateBack = onNavigateBack
            )
            is QuizUiState.Error -> QuizErrorSection(
                message = state.message,
                onNavigateBack = onNavigateBack
            )
        }
    }
}
