package ygmd.kmpquiz.core.domain.qanda

import kotlinx.serialization.Serializable

@Serializable
data class Qanda(
    val id: String,
    val question: QuestionContent,
    val answers: Answers,
    val categoryId: String,
) {
    val contextKey: String = "${question.contextKey}|${answers.contextKey}"
}

@Serializable
sealed interface QuestionContent {
    val contextKey: String
        get() = when (this) {
            is TextContent -> text
            is ImageContent -> imageUrl
        }

    @Serializable
    data class TextContent(val text: String) : QuestionContent
    @Serializable
    data class ImageContent(val imageUrl: String) : QuestionContent
}

@Serializable
data class Answers(
    val answerContents: List<AnswerContent>,
    val correctAnswer: AnswerContent,
): List<AnswerContent> by answerContents {
    val contextKey = correctAnswer.contextKey
}

@Serializable
sealed interface AnswerContent {
    val id: String

    val contextKey: String
        get() = when (this) {
            is TextAnswerContent -> text
            is ImageAnswerContent -> imageUrl
        }

    @Serializable
    data class TextAnswerContent(
        override val id: String,
        val text: String,
    ) : AnswerContent

    @Serializable
    data class ImageAnswerContent(
        override val id: String,
        val imageUrl: String,
        val altText: String? = null
    ) : AnswerContent
}