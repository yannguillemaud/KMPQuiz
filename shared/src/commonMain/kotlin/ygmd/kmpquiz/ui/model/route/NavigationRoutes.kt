package ygmd.kmpquiz.ui.model.route

import kotlinx.serialization.Serializable

// Routes de navigation
@Serializable
sealed class Route(val name: String) {

    @Serializable
    data object Home : Route("Home")

    @Serializable
    data object Fetch : Route("Fetch")

    @Serializable
    data object Categories : Route("Categories")

    @Serializable
    data object Settings : Route("Settings")

    @Serializable
    data object Quizzes : Route("Quizzes")

    @Serializable
    data class PlayQuiz(val quizId: String) : Route("PlayQuiz/$quizId")

    @Serializable
    data class QandaEdit(val qandaId: String) : Route("QandaEdit/$qandaId")

    @Serializable
    data class QandaCreation(val categoryId: String? = null) : Route(
        "QandaCreation${categoryId?.let { "/$it" }.orEmpty()}")

    @Serializable
    data class QuizSettings(val quizId: String) : Route("QuizSettings/$quizId")

    @Serializable
    data object QuizCreation : Route("QuizCreation")

    @Serializable
    data class Category(val categoryId: String) : Route("Category/$categoryId")
}