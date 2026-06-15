package ygmd.kmpquiz.core.domain.quiz.config

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
sealed class QuizQuestionsConfiguration {
    abstract val id: String

    @Serializable
    data class AllQuestions(
        override val id: String = UUID.randomUUID().toString(),
    ) : QuizQuestionsConfiguration()

    @Serializable
    data class TotalLimited(
        override val id: String = UUID.randomUUID().toString(),
        val count: Int = 0
    ) : QuizQuestionsConfiguration()

    @Serializable
    data class ByCategory(
        override val id: String = UUID.randomUUID().toString(),
        val limitByCategory: Map<String, Int> = emptyMap(),
    ) : QuizQuestionsConfiguration()
}