package ygmd.kmpquiz.domain.fetch.opentrivia

import kotlinx.serialization.Serializable
import ygmd.kmpquiz.domain.fetch.unescaped
import ygmd.kmpquiz.domain.pojo.InternalQanda

@Serializable
data class TriviaApiResponse(
    val response_code: Int,
    val results: List<QANDADto>
)

@Serializable
class QANDADto(
    val type: String,
    val difficulty: String,
    val category: String,
    val question: String,
    val correct_answer: String,
    val incorrect_answers: List<String>
) {
    fun toInternal(): InternalQanda {
        val incorrect = incorrect_answers.map { it.unescaped() }
        val correct = correct_answer.unescaped()

        val allAnswers =
            if (incorrect_answers.size == 1) incorrect_answers.asBooleanAnswers()
            else (incorrect + correct)

        return InternalQanda(
            category = category.sanitized().unescaped(),
            difficulty = difficulty,
            question = question.unescaped(),
            answers = allAnswers,
            correctAnswer = correct_answer,
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

        other as QANDADto

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

private fun List<String>.asBooleanAnswers(): List<String> = listOf(
    first(),
    first().toBoolean()
        .not()
        .toString()
        .replaceFirstChar { it.uppercase() }
)

private fun String.sanitized(): String = this.replace("Entertainment: ", "")