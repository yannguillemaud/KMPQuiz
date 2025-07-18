package ygmd.kmpquiz.viewModel.quiz

import ygmd.kmpquiz.domain.entities.qanda.AnswerContent
import ygmd.kmpquiz.domain.entities.quiz.QuizSession

sealed class QuizUiState {
    data object Idle : QuizUiState()

    data class InProgress(
        val session: QuizSession,
        val shuffledAnswers: List<AnswerContent> = emptyList(),
        val hasAnswered: Boolean = false,
        val selectedAnswer: AnswerContent? = null,
    ) : QuizUiState()

    data class Completed(
        val session: QuizSession,
        val results: QuizResult,
    ) : QuizUiState()

    data class Error(val message: String) : QuizUiState()
}

// TODO
data class QuizResult(
    val questions: Int,
    val score: Int,
) {
    val percentage: Int
        get() = if (questions > 0) (score * 100) / questions else 0
}