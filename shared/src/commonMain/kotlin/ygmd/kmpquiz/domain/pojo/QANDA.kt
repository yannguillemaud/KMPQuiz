package ygmd.kmpquiz.domain.pojo

data class QANDA(
    val id: Long,
    val category: String,
    val question: String,
    val answers: List<String>,
    val correctAnswer: String,
)
