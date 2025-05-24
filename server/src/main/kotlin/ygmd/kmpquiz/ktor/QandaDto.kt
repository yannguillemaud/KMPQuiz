package ygmd.kmpquiz.ktor

import kotlinx.serialization.Serializable

@Serializable
data class QandaDto(
    val id: Long? = null,
    val category: String,
    val question: String,
    val answers: List<String>,
    val correctAnswerPosition: Int
)