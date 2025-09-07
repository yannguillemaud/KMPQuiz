package ygmd.kmpquiz.viewModel.quiz.session

import ygmd.kmpquiz.domain.entities.qanda.Answers
import ygmd.kmpquiz.domain.entities.qanda.Choice
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.entities.quiz.QuizSession

sealed class QuizSessionUiState {
    data object Idle : QuizSessionUiState()

    data class InProgress(
        val session: QuizSession,
        val hasAnswered: Boolean = false,
        val selectedAnswer: Choice? = null,
        val shuffledAnswers: Answers,
    ) : QuizSessionUiState() {
        val currentQanda: Qanda
            get() = session.currentQanda ?: error("Current Qanda cannot be null")
    }

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