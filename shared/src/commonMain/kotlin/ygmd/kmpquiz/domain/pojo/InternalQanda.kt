package ygmd.kmpquiz.domain.pojo

data class InternalQanda(
    val id: Long? = null,
    val category: String,
    val question: String,
    val answers: List<String>,
    val correctAnswer: String,
    val difficulty: String,
){
    val contentKey: String
        get() = "${question.trim().lowercase()}|${correctAnswer.trim().lowercase()}"
}
