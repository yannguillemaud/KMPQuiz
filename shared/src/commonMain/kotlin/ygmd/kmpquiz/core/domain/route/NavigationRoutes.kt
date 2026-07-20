package ygmd.kmpquiz.core.domain.route

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface KMPQuizRoute : NavKey {
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
        val fromNotification: Boolean = false,
    ) : KMPQuizRoute

    @Serializable
    data class Category(val categoryId: String) : KMPQuizRoute

    @Serializable
    data class QuizEditor(val quizId: String? = null) : KMPQuizRoute

    @Serializable
    data object SessionHistory: KMPQuizRoute

    @Serializable
    data class SessionDetails(val sessionId: String): KMPQuizRoute

    @Serializable
    data class CategoryReview(
        val sessionId: String,
        val categoryId: String,
        val categoryName: String,
    ) : KMPQuizRoute
}