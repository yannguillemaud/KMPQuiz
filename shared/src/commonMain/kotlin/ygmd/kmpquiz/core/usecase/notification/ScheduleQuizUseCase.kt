package ygmd.kmpquiz.core.usecase.notification

import co.touchlab.kermit.Logger
import ygmd.kmpquiz.core.repository.QuizRepository
import ygmd.kmpquiz.core.service.scheduler.QuizAlarmScheduler
import ygmd.kmpquiz.core.usecase.scheduler.ComputeQuizSchedulerNextTriggerUseCase

private val logger = Logger.withTag("ScheduleQuizUseCase")

/**
 * Scheduling logic
 */
class ScheduleQuizUseCase(
    private val quizRepository: QuizRepository,
    private val computeQuizSchedulerNextTriggerUseCase: ComputeQuizSchedulerNextTriggerUseCase,
    private val alarmScheduler: QuizAlarmScheduler,
) {
    /**
     * cancel quiz scheduling
     */
    fun cancel(quizId: String) {
        alarmScheduler.cancelAlarm(quizId)
    }

    /**
     * schedule next occurrence of a quiz
     */
    suspend fun schedule(quizId: String): Result<Unit> {
        val quiz = quizRepository.getById(quizId).getOrThrow()
        val nextTrigger = computeQuizSchedulerNextTriggerUseCase(quiz) ?: return Result.failure(
            Exception("Failed to compute next trigger for quiz: $quizId.")
        )
        return alarmScheduler.scheduleAlarm(quizId, nextTrigger.toEpochMilliseconds())
    }
}

