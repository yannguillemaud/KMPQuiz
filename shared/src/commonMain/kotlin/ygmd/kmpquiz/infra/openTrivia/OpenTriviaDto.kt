package ygmd.kmpquiz.infra.openTrivia

import kotlinx.serialization.Serializable
import ygmd.kmpquiz.domain.entities.qanda.AnswerSet
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.entities.qanda.QandaMetadata
import ygmd.kmpquiz.domain.entities.qanda.QuestionContent
import ygmd.kmpquiz.unescaped

@Serializable
data class OpenTriviaApiResponse(
    val response_code: Int,
    val results: List<OpenTriviaQandaDto>
)

@Serializable
class OpenTriviaQandaDto(
    val type: String,
    val difficulty: String,
    val category: String,
    val question: String,
    val correct_answer: String,
    val incorrect_answers: List<String>
) {
    fun toQanda(): Qanda {
        val incorrect = incorrect_answers.map { it.unescaped() }
        val correct = correct_answer.unescaped()
        val isBooleanQuestion = incorrect.size == 1

        return Qanda(
            question = QuestionContent.TextContent(question),
            answers = answerSetOf(isBooleanQuestion, correct),
            metadata = QandaMetadata(
                category = category.sanitized(),
                difficulty = difficulty,
            )
        )
    }

    override fun toString(): String {
        return "QANDADto(" +
                "type='$type', " +
                "difficulty='$difficulty', " +
                "category='$category', " +
                "question='$question', " +
                "correct_answer='$correct_answer', " +
                "incorrect_answers=$incorrect_answers" +
                ")"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OpenTriviaQandaDto

        if (type != other.type) return false
        if (category != other.category) return false
        if (question != other.question) return false
        if (correct_answer != other.correct_answer) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + question.hashCode()
        result = 31 * result + correct_answer.hashCode()
        return result
    }
}

private fun OpenTriviaQandaDto.answerSetOf(
    isBooleanQuestion: Boolean,
    correct: String
): AnswerSet = if (isBooleanQuestion) {
    AnswerSet.createTrueFalse(correct_answer.toBooleanStrict())
} else {
    AnswerSet.createMultipleTextChoice(correct, incorrect_answers)
}


private fun String.sanitized(): String = this.replace("Entertainment: ", "")