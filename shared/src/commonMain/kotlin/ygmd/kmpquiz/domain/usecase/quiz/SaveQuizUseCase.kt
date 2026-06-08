package ygmd.kmpquiz.domain.usecase.quiz

import co.touchlab.kermit.Logger
import ygmd.kmpquiz.domain.model.scheduler.SchedulerConfiguration
import ygmd.kmpquiz.domain.model.scheduler.SchedulerSelection
import ygmd.kmpquiz.domain.model.quiz.Quiz
import ygmd.kmpquiz.domain.repository.QuizRepository
import ygmd.kmpquiz.domain.repository.SchedulerDataStore
import ygmd.kmpquiz.domain.scheduler.QuizScheduler
import ygmd.kmpquiz.domain.service.CronSchedulerHelper

private val logger = Logger.withTag("SaveQuizUseCase")

class SaveQuizUseCase(
    private val quizRepository: QuizRepository,
    private val schedulerStore: SchedulerDataStore,
    private val alarmScheduler: QuizScheduler,
) {
    /**
     * Saves a quiz and schedules it if it has a configuration enabled.
     * If no configuration or disabled, cancels it.
     * @param quiz The quiz to save
     */
    suspend fun save(quiz: Quiz): Result<Unit> =
        quizRepository.saveQuiz(quiz).onSuccess {
            val shouldSchedule = quiz.schedulerConfiguration?.isEnabled == true
            if (shouldSchedule) handleQuizScheduling(
                quizId = quiz.id,
                configuration = quiz.schedulerConfiguration
            ) else cancelIfScheduled(quiz.id)
        }

    /**
     * Cancels a quiz.
     * Removes the configuration if it exists in the scheduler store.
     * @param quizId The id of the quiz to cancel
     */
    private suspend fun cancelIfScheduled(quizId: String) {
        schedulerStore.getConfiguration(quizId)?.let {
            schedulerStore.removeConfiguration(quizId)
            logger.d { "Removed configuration for $quizId" }
        }
        alarmScheduler.cancelAlarm(quizId)
        logger.d { "Cancelled alarm for $quizId" }
    }

    /**
     * Schedules a quiz.
     * Registers the configuration if not already registered.
     * @param quizId The id of the quiz to schedule
     * @param configuration The configuration to schedule the quiz with
     */
    private suspend fun handleQuizScheduling(
        quizId: String,
        configuration: SchedulerConfiguration
    ) {
        if (configuration.selection !is SchedulerSelection.SpecificTime) {
            logger.w { "Scheduler is not yet handled: $quizId. Aborting" }
            return
        }
        schedulerStore.saveConfiguration(quizId, configuration)
        logger.d { "Saved configuration for $quizId" }
        alarmScheduler.scheduleAlarm(
            quizId = quizId,
            exactTimestampEpochMillis = CronSchedulerHelper.computeNextTrigger(configuration.selection)
                .toEpochMilliseconds()
        )
        logger.d { "Scheduled alarm for $quizId" }
    }
}

