package ygmd.kmpquiz.domain.entities.qanda

import kotlinx.serialization.Serializable

@Serializable
data class Qanda(
    val id: String,
    val question: Question,
    val answers: Answers,
    val metadata: Metadata,
) {
    val contextKey: String = "${question.contextKey}|${answers.contextKey}"
    val correctAnswer: Choice = answers.correctAnswer
}