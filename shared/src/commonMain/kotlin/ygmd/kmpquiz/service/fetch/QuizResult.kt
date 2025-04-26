package ygmd.kmpquiz.service.fetch

import kotlinx.serialization.Serializable

@Serializable
class QuizResult(
    val status: String,
    val question: String,
    val reponse: String,
)