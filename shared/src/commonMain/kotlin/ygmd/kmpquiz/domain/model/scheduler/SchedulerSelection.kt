package ygmd.kmpquiz.domain.model.scheduler

import kotlinx.serialization.Serializable
import java.time.LocalTime
import java.time.LocalTime.of

@Serializable
sealed interface SchedulerSelection {
    @Serializable
    data class SpecificTime(val hour: Int, val minute: Int) : SchedulerSelection {
        val localTime: LocalTime get() = of(hour, minute)
    }

    @Serializable
    data class TimeRange(
        val startHour: Int,
        val startMinute: Int,
        val endHour: Int,
        val endMinute: Int
    ) : SchedulerSelection

    val explicitName: String
        get() = when (this) {
            is SpecificTime -> "At: ${of(hour, minute)}"
            is TimeRange -> "Between: $startHour:$startMinute - $endHour:$endMinute"
        }
}