package ygmd.kmpquiz.domain.model.qanda

import kotlinx.serialization.Serializable

@Serializable
data class Qanda(
    val id: String,
    val question: Question,
    val answers: Answers,
    val categoryId: String,
    val metadata: Metadata = Metadata(),
) {
    val contextKey: String = "${question.contextKey}|${answers.contextKey}"
    val correctAnswer: Choice = answers.correctAnswer
}
