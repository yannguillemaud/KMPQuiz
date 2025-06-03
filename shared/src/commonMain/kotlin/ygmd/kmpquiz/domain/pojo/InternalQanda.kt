package ygmd.kmpquiz.domain.pojo

data class InternalQanda(
    val id: Long? = null,
    val category: String,
    val question: String,
    val answers: List<String>,
    val correctAnswerPosition: Int,
    val difficulty: String,
)

fun InternalQanda.correctAnswer(): String = answers[correctAnswerPosition]
fun InternalQanda.contentKey(): String =
    "${question.trim().lowercase()}|${correctAnswer().trim().lowercase()}"
