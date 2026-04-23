package ygmd.kmpquiz.domain.model.cron

import kotlinx.serialization.Serializable

@Serializable
data class QuizCron(
    val id: String,
    val name: String,
    val expression: String,
    val isEnabled: Boolean = false,
)