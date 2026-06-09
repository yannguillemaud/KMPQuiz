package ygmd.kmpquiz.core.usecase.scheduler

import co.touchlab.kermit.Logger
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toKotlinLocalDateTime
import ygmd.kmpquiz.core.domain.quiz.Quiz
import ygmd.kmpquiz.core.domain.quiz.config.SchedulerSelection
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import kotlin.time.Instant

private val logger = Logger.withTag("ComputeSchedulerNextTriggerUseCase")

class ComputeQuizSchedulerNextTriggerUseCase {
    operator fun invoke(quiz: Quiz): Instant? {
        val configuration = quiz.schedulerConfiguration ?: run {
            logger.e { "Cannot compute next trigger because configuration of quiz ${quiz.id} is null." }
            return null
        }
        return when (configuration.selection) {
            is SchedulerSelection.SpecificTime -> nextTrigger(configuration.selection)
            is SchedulerSelection.TimeRange -> {
                logger.w { "TimeRange is not yet handled." }
                null
            }
        }
    }

    private fun nextTrigger(selection: SchedulerSelection.SpecificTime): Instant {
        val selectionTime = selection.localTime
        val timeZone = TimeZone.currentSystemDefault()
        val zoneId = ZoneId.systemDefault()
        val nowLocalDateTime = LocalDateTime.now(zoneId)
        if (selection.localTime.isAfter(LocalTime.now(zoneId))) {
            val nexTrigger = nowLocalDateTime
                .withHour(selectionTime.hour)
                .withMinute(selectionTime.minute)
            return nexTrigger.toKotlinLocalDateTime().toInstant(timeZone)
        } else {
            val nexTrigger = nowLocalDateTime
                .plusDays(1)
                .withHour(selectionTime.hour)
                .withMinute(selectionTime.minute)
            return nexTrigger.toKotlinLocalDateTime().toInstant(timeZone)
        }
    }
}