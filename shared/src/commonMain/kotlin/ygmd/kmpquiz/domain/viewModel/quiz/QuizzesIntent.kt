package ygmd.kmpquiz.domain.viewModel.quiz

sealed interface QuizzesIntent {
    data class DeleteQuiz(val quizId: String) : QuizzesIntent
    data class ToggleCron(val quizId: String, val isEnabled: Boolean): QuizzesIntent
}