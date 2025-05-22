package ygmd.kmpquiz.domain.pojo

data class QANDA(
    val category: String,
    val question: String,
    val answers: List<String>,
    val correctAnswer: String,
)
