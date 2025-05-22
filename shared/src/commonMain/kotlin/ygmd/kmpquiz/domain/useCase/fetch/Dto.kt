package ygmd.kmpquiz.domain.useCase.fetch

import kotlinx.serialization.Serializable
import ygmd.kmpquiz.domain.pojo.QANDA

@Serializable
data class QuizResultDto(
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
){
    fun toQanda(): QANDA {
        return QANDA(
            question = question.unescaped(),
            correctAnswer = correct_answer.unescaped(),
            answers = if(type == "boolean") incorrect_answers.asBooleanAnswers()
                else (incorrect_answers + correct_answer).map { it.unescaped() },
            category = category.unescaped().toInternalCategory()
        )
    }

    override fun toString(): String {
        return "QANDADto(type='$type', difficulty='$difficulty', category='$category', question='$question', correct_answer='$correct_answer', incorrect_answers=$incorrect_answers)"
    }
}

private fun List<String>.asBooleanAnswers(): List<String> = listOf(
    first(),
    first().toBoolean()
        .not()
        .toString()
        .replaceFirstChar { it.uppercase() }
)

private fun String.toInternalCategory(): String = this.replace("Entertainment: ", "")