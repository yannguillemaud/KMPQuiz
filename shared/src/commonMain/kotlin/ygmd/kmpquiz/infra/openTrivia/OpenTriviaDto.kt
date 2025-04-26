package ygmd.kmpquiz.infra.openTrivia

import kotlinx.serialization.Serializable

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
}




