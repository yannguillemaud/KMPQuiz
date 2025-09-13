package ygmd.kmpquiz.ui.model.route

import kotlinx.serialization.Serializable

// Routes de navigation
@Serializable
sealed class Route(val name: String) {

    @Serializable
    data object Home : Route("Home")

    @Serializable
    data object Fetch: Route("Fetch")

    @Serializable
    data object Qandas: Route("Qandas")

    @Serializable
    data object Settings: Route("Settings")

    @Serializable
    data object Quizzes: Route("Quizzes")

    @Serializable
    data object QuizCreation

    @Serializable
    data class PlayQuiz(val quizId: String)
}