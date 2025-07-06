package ygmd.kmpquiz.android.ui.composable.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ygmd.kmpquiz.domain.entities.qanda.AnswerSet.AnswerContent
import ygmd.kmpquiz.viewModel.quiz.QuizUiState

@Composable
fun QuizInProgressSection(
    state: QuizUiState.InProgress,
    onAnswerSelected: (AnswerContent) -> Unit,
    onNextQuestion: () -> Unit,
    onNavigateBack: () -> Unit,
){
    val session = state.session
    val currentQanda = session.currentQanda!!

    Column(modifier = Modifier.fillMaxSize()) {
        // Header avec progression
        QuizHeader(
            currentQuestion = session.currentIndex + 1,
            totalQuestions = session.qandas.size,
            // TODO
            category = currentQanda.metadata.category ?: "NullCategory",
            onNavigateBack = onNavigateBack
        )

        // Corps principal scrollable
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                QuestionCard(question = currentQanda.question.text)
            }

// TODO
/*
            items(state.shuffledAnswers) { answer ->
                AnswerCard(
                    answer = answer,
                    isSelected = answer == state.selectedAnswer,
                    isAnswered = state.hasAnswered,
                    isCorrect = if (state.hasAnswered) answer == currentQanda.correctAnswer else null,
                    onClick = { onAnswerSelected(answer) }
                )
            }
*/
        }

        // Footer avec bouton suivant
        if (state.hasAnswered) {
            NextQuestionButton(
                isComplete = session.isComplete,
                onClick = onNextQuestion
            )
        }
    }
}