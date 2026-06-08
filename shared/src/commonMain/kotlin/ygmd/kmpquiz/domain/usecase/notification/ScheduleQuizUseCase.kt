package ygmd.kmpquiz.domain.usecase.notification

import co.touchlab.kermit.Logger
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ygmd.kmpquiz.domain.model.scheduler.SchedulerConfiguration
import ygmd.kmpquiz.domain.model.scheduler.SchedulerSelection
import ygmd.kmpquiz.domain.repository.SchedulerDataStore
import ygmd.kmpquiz.domain.scheduler.QuizScheduler
import ygmd.kmpquiz.domain.service.CronSchedulerHelper.computeNextTrigger

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
    suspend fun register(quizId: String, config: SchedulerConfiguration) {
        schedulerStore.saveConfiguration(quizId, config)
    }

    /**
     * returns true if a quiz is scheduled, false otherwise
     */
    suspend fun isRegistered(quizId: String): Boolean =
        schedulerStore.getConfiguration(quizId) != null

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
    fun schedule(id: String, scheduler: SchedulerSelection): Result<Unit> {
        if (scheduler !is SchedulerSelection.SpecificTime) {
            logger.w { "Scheduler is not yet handled: $id. Aborting" }
            return Result.failure(RuntimeException("Scheduler is not yet handled"))
        }
        val nextTrigger = computeNextTrigger(scheduler)
        logger.i { "Scheduling quiz: $id at ${nextTrigger.toLocalDateTime(TimeZone.currentSystemDefault())}" }
        alarmScheduler.scheduleAlarm(id, nextTrigger.toEpochMilliseconds())
        return Result.success(Unit)
    }
}

