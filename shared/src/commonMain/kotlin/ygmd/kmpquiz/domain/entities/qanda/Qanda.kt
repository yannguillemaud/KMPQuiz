package ygmd.kmpquiz.domain.entities.qanda

import kotlinx.serialization.Serializable
import ygmd.kmpquiz.domain.entities.qanda.AnswerSet.AnswerContent

@Serializable
data class Qanda(
    val id: Long,
    val question: QuestionContent,
    val answers: AnswerSet,
    val metadata: QandaMetadata,
) {
    init {
        require(answers.hasExactlyOneCorrectAnswer()) {
            "Une question doit avoir exactement une seule réponse correcte"
        }
    }

    val contextKey: String
        get() = "${question.contextKey}|${correctAnswer.contextKey}"

    val correctAnswer: AnswerContent
        get() = answers.correctAnswer
}


@Serializable
data class QandaMetadata(
    val category: String,
    val difficulty: String?,
    val tags: Map<String, String> = emptyMap()
)

@Serializable
sealed interface QuestionContent {
    val text: String
    val contextKey: String

    @Serializable
    data class TextContent(override val text: String) : QuestionContent {
        override val contextKey: String
            get() = text.normalize()
    }

    @Serializable
    data class ImageContent(
        override val text: String,
        val imageUrl: String,
        val altText: String? = null
    ) : QuestionContent {
        override val contextKey: String
            get() = "${text}|${imageUrl}".normalize()
    }
}


@Serializable
data class AnswerSet(
    val answers: List<AnswerContent>
) {
    init {
        require(answers.isNotEmpty())
        require(answers.size >= 2)
    }

    val correctAnswer: AnswerContent
        get() = answers.first { it.isCorrect }

    val incorrectAnswers: List<AnswerContent>
        get() = answers.filterNot { it.isCorrect }

    fun hasExactlyOneCorrectAnswer(): Boolean = answers.count { it.isCorrect } == 1
    fun shuffled(): AnswerSet = copy(answers = answers.shuffled())

    companion object {
        fun createMultipleTextChoice(
            correctAnswer: String,
            incorrectAnswers: List<String>
        ): AnswerSet {
            require(incorrectAnswers.isNotEmpty()) { "Il faut au moins une mauvaise réponse" }

            val allAnswers = listOf(
                AnswerContent.TextContent(correctAnswer, isCorrect = true)
            ) + incorrectAnswers.map {
                AnswerContent.TextContent(it, isCorrect = false)
            }

            return AnswerSet(allAnswers)
        }

        fun createTrueFalse(correctAnswer: Boolean): AnswerSet {
            return AnswerSet(
                listOf(
                    AnswerContent.TextContent("True", isCorrect = correctAnswer),
                    AnswerContent.TextContent("False", isCorrect = !correctAnswer)
                )
            )
        }
    }

    @Serializable
    sealed interface AnswerContent {
        val isCorrect: Boolean
        val contextKey: String

        @Serializable
        data class TextContent(
            val text: String,
            override val isCorrect: Boolean,
        ) : AnswerContent {
            init {
                require(text.isNotBlank())
            }

            override val contextKey: String
                get() = text.normalize()
        }

        @Serializable
        data class ImageContent(
            val imageUrl: String,
            val altText: String? = null,
            override val isCorrect: Boolean,
        ) : AnswerContent {
            init {
                require(imageUrl.isNotBlank())
            }

            override val contextKey: String
                get() = imageUrl.normalize()
        }
    }
}

private fun String.normalize(): String =
    trim()
        .lowercase()
        .replace(Regex("\\s+"), " ")