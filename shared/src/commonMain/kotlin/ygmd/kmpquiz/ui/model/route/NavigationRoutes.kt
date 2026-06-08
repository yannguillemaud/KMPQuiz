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
    data class PlaySession(
        val quizId: String? = null,
        val sessionId: String? = null,
    ) : Route()

    @Serializable
    data class Category(val categoryId: String) : Route()

    @Serializable
    data class QuizEditor(val quizId: String? = null) : Route()

    @Serializable
    data object SessionHistory: Route()

    @Serializable
    data class SessionDetails(val sessionId: String): Route()
}