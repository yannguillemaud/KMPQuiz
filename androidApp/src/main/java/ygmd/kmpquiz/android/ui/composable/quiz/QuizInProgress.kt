package ygmd.kmpquiz.android.ui.composable.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ygmd.kmpquiz.domain.entities.qanda.AnswerSet.AnswerContent
import ygmd.kmpquiz.viewModel.quiz.session.QuizSessionUiState

@Composable
fun QuizInProgressSection(
    quiz: QuizSessionUiState.InProgress,
    onAnswerSelected: (AnswerContent) -> Unit,
    onNextQuestion: () -> Unit,
    onNavigateBack: () -> Unit,
){
    val session = quiz.session
    val currentQanda = session.currentQanda!!

    Column(modifier = Modifier.fillMaxSize()) {
        QuizHeader(
            currentQuestion = session.currentIndex + 1,
            totalQuestions = session.qandas.size,
            // TODO
            category = currentQanda.metadata.category ?: "NullCategory",
            onNavigateBack = onNavigateBack
        )

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

            items(requireNotNull(quiz.shuffledAnswers).answers) { answer ->
                AnswerCard(
                    answer = answer,
                    isSelected = answer == quiz.selectedAnswer,
                    isAnswered = quiz.hasAnswered,
                    isCorrect = if (quiz.hasAnswered) answer == currentQanda.correctAnswer else null,
                    onClick = { onAnswerSelected(answer) }
                )
            }
        }

        // Footer avec bouton suivant
        if (quiz.hasAnswered) {
            NextQuestionButton(
                isComplete = session.isComplete,
                onClick = onNextQuestion
            )
        }
    }
}