package ygmd.kmpquiz.domain.pojo

data class InternalQanda(
    val id: Long = 0,
    val category: String,
    val question: String,
    val answers: List<String>,
    val correctAnswer: String,
)
