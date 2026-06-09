package ygmd.kmpquiz.core.domain.quiz.config

import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable
data class QuizSchedulerConfiguration(
    val id: String,
    val selection: SchedulerSelection,
    val isEnabled: Boolean
)

@Serializable
sealed interface SchedulerSelection {
    @Serializable
    data class SpecificTime(val hour: Int, val minute: Int) : SchedulerSelection {
        val localTime: LocalTime get() = LocalTime.of(hour, minute)
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
            is SpecificTime -> "At: ${LocalTime.of(hour, minute)}"
            is TimeRange -> "Between: $startHour:$startMinute - $endHour:$endMinute"
        }
}