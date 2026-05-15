package ygmd.kmpquiz.ui.model.route

import kotlinx.serialization.Serializable

// Routes de navigation
@Serializable
sealed class Route() {
    @Serializable
    data object Home : Route()

    @Serializable
    data object Categories : Route()

    @Serializable
    data object Quizzes : Route()

    @Serializable
    data class PlayQuiz(val quizId: String) : Route()

    @Serializable
    data class QandaEdit(val qandaId: String) : Route()

    @Serializable
    data class QandaCreation(val categoryId: String? = null) : Route()

    @Serializable
    data class Category(val categoryId: String) : Route()

    @Serializable
    data class QuizEditor(val quizToEdit: String? = null) : Route()
}