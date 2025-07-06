package ygmd.kmpquiz.domain.entities.qanda

import ygmd.kmpquiz.domain.entities.qanda.AnswerSet.AnswerContent


data class Qanda(
    val id: Long? = null,
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


data class QandaMetadata(
    val category: String?,
    val difficulty: String?,
    val tags: Map<String, Any> = emptyMap()
)


sealed interface QuestionContent {
    val text: String
    val contextKey: String

    data class TextContent(override val text: String) : QuestionContent {
        override val contextKey: String
            get() = text.normalize()
    }


    data class ImageContent(
        override val text: String,
        val imageUrl: String,
        val altText: String? = null
    ) : QuestionContent {
        override val contextKey: String
            get() = "${text}|${imageUrl}".normalize()
    }
}


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

    val allAnswersText: List<String>
        get() = answers.map { it.contextKey }

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


    sealed interface AnswerContent {
        val isCorrect: Boolean
        val contextKey: String


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