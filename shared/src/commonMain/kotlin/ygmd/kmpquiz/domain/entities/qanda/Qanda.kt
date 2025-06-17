package ygmd.kmpquiz.domain.entities.qanda

data class Qanda(
    val id: Long? = null,
    val category: String,
    val difficulty: String,
    val qandaQuestion: QuestionType,
    val answers: List<AnswerContent>,
) {
    init {
        require(answers.count { it.isCorrect } == 1) {
            "Il doit y avoir exactement une réponse correcte"
        }
    }

    val correctAnswer get() = answers.first { it.isCorrect }

    val contextKey: String
        get() = "${qandaQuestion.contextKey}|${correctAnswer.contextKey}"

    val question: String = when (qandaQuestion) {
        is QuestionType.TextQuestion -> qandaQuestion.text
        is QuestionType.ImageQuestion -> qandaQuestion.text
    }

    val textAnswers = answers.map { it.contextKey }

    fun checkAnswer(selected: String) = when (correctAnswer) {
        is AnswerContent.TextAnswer -> selected == (correctAnswer as AnswerContent.TextAnswer).text
        is AnswerContent.ImageAnswer -> selected == (correctAnswer as AnswerContent.ImageAnswer).image.url
    }
}


sealed interface QuestionType {
    val contextKey: String

    data class TextQuestion(val text: String) : QuestionType {
        override val contextKey: String = text.lowercase()
    }

    data class ImageQuestion(val text: String, val image: Image) : QuestionType {
        override val contextKey: String = "$text|${image.url}".lowercase()
    }
}

sealed interface AnswerContent {
    val isCorrect: Boolean
    val contextKey: String

    data class TextAnswer(
        val text: String,
        override val isCorrect: Boolean
    ) : AnswerContent {
        override val contextKey: String get() = text.lowercase()
    }

    data class ImageAnswer(
        val image: Image,
        override val isCorrect: Boolean
    ) : AnswerContent {
        override val contextKey: String get() = image.url.lowercase()
    }
}

data class Image(
    val url: String,
    val altText: String? = null,
)

fun List<AnswerContent>.contains(key: String) = any {
    it.contextKey.equals(key, ignoreCase = true)
}