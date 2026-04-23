package ygmd.kmpquiz.domain.model.quiz

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
sealed class QuizConfigDetails {
    abstract val id: String

    @Serializable
    data class AllQuestions(
        override val id: String = UUID.randomUUID().toString(),
    ) : QuizConfigDetails()

    @Serializable
    data class TotalLimited(
        override val id: String = UUID.randomUUID().toString(),
        val count: Int = 0
    ) : QuizConfigDetails()

    @Serializable
    data class ByCategory(
        override val id: String = UUID.randomUUID().toString(),
        val limitByCategory: Map<String, Int> = emptyMap(),
    ) : QuizConfigDetails()
}