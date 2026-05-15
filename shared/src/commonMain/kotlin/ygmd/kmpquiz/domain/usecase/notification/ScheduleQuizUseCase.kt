package ygmd.kmpquiz.domain.usecase.notification

import co.touchlab.kermit.Logger
import com.ucasoft.kcron.Cron
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import ygmd.kmpquiz.domain.model.cron.SchedulerConfiguration
import ygmd.kmpquiz.domain.repository.SchedulerDataStore
import ygmd.kmpquiz.domain.scheduler.QuizScheduler
import java.time.Instant.ofEpochMilli
import java.time.LocalDateTime.ofInstant
import java.time.ZoneId.systemDefault

private val logger = Logger.withTag("ScheduleQuizUseCase")

/**
 * Scheduling logic
 */
class ScheduleQuizUseCase(
    private val schedulerStore: SchedulerDataStore,
    private val alarmScheduler: QuizScheduler,
) {
    /**
     * configure and schedule a quiz.
     * current implementation cancels existing scheduling if exists and schedules a new one.
     */
    suspend fun configureAndSchedule(quizId: String, config: SchedulerConfiguration) {
        schedulerStore.saveConfiguration(quizId, config)
        schedule(quizId, config)
    }

    /**
     * returns true if a quiz is scheduled, false otherwise
     */
    suspend fun isScheduled(quizId: String): Boolean =
        schedulerStore.getConfiguration(quizId) != null

    /**
     * returns true if a quiz is scheduled and differs from the parameter, false otherwise
     */
    suspend fun differs(quizId: String, config: SchedulerConfiguration): Boolean {
        val existingConfig = schedulerStore.getConfiguration(quizId)
        return existingConfig != null && existingConfig.selection != config.selection
    }

    /**
     * cancel quiz scheduling
     */
    suspend fun cancel(quizId: String) {
        schedulerStore.removeConfiguration(quizId)
        alarmScheduler.cancelAlarm(quizId)
    }

    /**
     * schedule next occurrence of a quiz
     */
    fun schedule(quizId: String, config: SchedulerConfiguration) {
        val nextTriggerMillis = calculateNextTriggerTime(config)
        if (nextTriggerMillis != null) {
            alarmScheduler.scheduleAlarm(quizId, nextTriggerMillis)
            logger.d {
                "Scheduled alarm for $quizId at ${
                    ofInstant(ofEpochMilli(nextTriggerMillis), systemDefault())
                }"
            }
        } else {
            logger.w { "Failed to schedule alarm for $quizId" }
        }
    }

    private fun calculateNextTriggerTime(config: SchedulerConfiguration): Long? {
        if (!config.isEnabled) return null
        return try {
            Cron.parseAndBuild(config.cron)
                .nextRun
                ?.toInstant(TimeZone.currentSystemDefault())
                ?.toEpochMilliseconds()
                ?: error("Cannot find next execution for cron: ${config.cron}")
        } catch (e: Exception) {
            logger.e(e) { "Failed to calculate next trigger time" }
            null
        }
    }
}