package ygmd.kmpquiz.domain.model.cron

import kotlinx.serialization.Serializable

@Serializable
data class ScheduledCrons(
    val crons: Map<String, ScheduledCronState> = emptyMap()
)

/**
 * Represents a cron's state in the store
 */
@Serializable
data class ScheduledCronState(
    val expression: String,
    val isEnabled: Boolean
)