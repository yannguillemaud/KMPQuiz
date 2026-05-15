package ygmd.kmpquiz.domain.model.cron

import kotlinx.serialization.Serializable

@Serializable
sealed interface SchedulerSelection {
    @Serializable
    data class SpecificTime(val hour: Int, val minute: Int) : SchedulerSelection

    @Serializable
    data class TimeRange(
        val startHour: Int,
        val startMinute: Int,
        val endHour: Int,
        val endMinute: Int
    ) : SchedulerSelection

    val explicitName: String
        get() = when (this) {
            is SpecificTime -> "At: $hour:$minute"
            is TimeRange -> "Between: $startHour:$startMinute - $endHour:$endMinute"
        }
}