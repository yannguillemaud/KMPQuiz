package ygmd.kmpquiz.viewModel.quiz.session

import ygmd.kmpquiz.domain.entities.qanda.Answers
import ygmd.kmpquiz.domain.entities.qanda.Choice
import ygmd.kmpquiz.domain.entities.quiz.QuizSession

sealed class QuizSessionUiState {
    data object Idle : QuizSessionUiState()

    data class InProgress(
        val session: QuizSession,
        val shuffledAnswers: Answers?,
        val hasAnswered: Boolean = false,
        val selectedAnswer: Choice? = null,
    ) : QuizSessionUiState()

    data class Completed(
        val session: QuizSession,
        val results: QuizResult,
    ) : QuizSessionUiState()

    data class Error(val message: String) : QuizSessionUiState()
}

// TODO
data class QuizResult(
    val questions: Int,
    val score: Int,
) {
    val percentage: Int
        get() = if (questions > 0) (score * 100) / questions else 0
}