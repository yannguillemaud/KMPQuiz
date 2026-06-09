package ygmd.kmpquiz.core.domain.route

import kotlinx.serialization.Serializable

@Serializable
sealed interface KMPQuizRoute {
    @Serializable
    data object Home : KMPQuizRoute

    @Serializable
    data object Categories : KMPQuizRoute

    @Serializable
    data object Quizzes : KMPQuizRoute

    @Serializable
    data class PlaySession(
        val quizId: String? = null,
        val sessionId: String? = null,
    ) : KMPQuizRoute

    @Serializable
    data class Category(val categoryId: String) : KMPQuizRoute

    @Serializable
    data class QuizEditor(val quizId: String? = null) : KMPQuizRoute

    @Serializable
    data object SessionHistory: KMPQuizRoute

    @Serializable
    data class SessionDetails(val sessionId: String): KMPQuizRoute
}