package ygmd.kmpquiz.domain.model.scheduler

import kotlinx.serialization.Serializable

@Serializable
data class SchedulerConfiguration(
    val id: String,
    val selection: SchedulerSelection,
    val isEnabled: Boolean
)