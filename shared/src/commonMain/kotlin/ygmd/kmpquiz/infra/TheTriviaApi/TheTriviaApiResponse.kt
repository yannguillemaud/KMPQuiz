package ygmd.kmpquiz.infra.TheTriviaApi

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class TheTriviaApiResponse(val value: Array<TheTriviaDto>)

@Serializable
data class TheTriviaDto(
    val id: String,
    val category: String,
    val difficulty: String,
    val question: QuestionDto,
    val correctAnswer: String,
    val incorrectAnswers: List<String>,

    val tags: List<String>,
    val type: String,
    val isNiche: Boolean,
)

@Serializable
data class QuestionDto(val text: String)
