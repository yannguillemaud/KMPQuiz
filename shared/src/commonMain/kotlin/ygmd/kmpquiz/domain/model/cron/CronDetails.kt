package ygmd.kmpquiz.domain.model.cron

import kotlinx.serialization.Serializable

@Serializable
data class CronDetails(
    val cronExpression: String,
    val isEnabled: Boolean
)

@Serializable
data class ScheduledCrons(
    val crons: Map<String, CronDetails> = emptyMap()
)
