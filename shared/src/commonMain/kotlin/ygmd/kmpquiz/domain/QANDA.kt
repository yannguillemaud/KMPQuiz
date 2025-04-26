package ygmd.kmpquiz.domain

data class QANDA(
    val question: String,
    val answers: List<String>,
    val correctAnswer: String,
)
