package ygmd.kmpquiz.core.usecase.quiz

import co.touchlab.kermit.Logger
import ygmd.kmpquiz.core.domain.quiz.Quiz
import ygmd.kmpquiz.core.repository.QuizRepository
import ygmd.kmpquiz.core.service.scheduler.QuizAlarmScheduler
import ygmd.kmpquiz.core.usecase.scheduler.ComputeQuizSchedulerNextTriggerUseCase

private val logger = Logger.withTag("SaveQuizUseCase")

class SaveQuizUseCase(
    private val quizRepository: QuizRepository,
    private val computeQuizSchedulerNextTriggerUseCase: ComputeQuizSchedulerNextTriggerUseCase,
    private val alarmScheduler: QuizAlarmScheduler,
) {
    /**
     * Saves a quiz and schedules it if it has a configuration set and enabled.
     * @param quiz The quiz to save
     */
    suspend fun save(quiz: Quiz): Result<Unit> =
        quizRepository.saveQuiz(quiz).onSuccess {
            val shouldSchedule = quiz.schedulerConfiguration?.isEnabled == true
            if (shouldSchedule) handleQuizScheduling(
                quizId = quiz.id
            ) else cancelIfScheduled(quiz.id)
        }

    /**
     * Cancels a quiz.
     * Removes the configuration if it exists in the scheduler store.
     * @param quizId The id of the quiz to cancel
     */
    private fun cancelIfScheduled(quizId: String) {
        alarmScheduler.cancelAlarm(quizId)
        logger.d { "Cancelled alarm for $quizId" }
    }

    private suspend fun handleQuizScheduling(quizId: String) {
        val quiz = quizRepository.getById(quizId).getOrThrow()
        val nextTrigger = computeQuizSchedulerNextTriggerUseCase.invoke(quiz) ?: run {
            logger.e { "Failed to compute next trigger for quiz $quiz" }
            return
        }
        alarmScheduler.scheduleAlarm(
            quizId = quizId,
            exactEpochMillis = nextTrigger.toEpochMilliseconds()
        )
        logger.d { "Scheduled alarm for $quizId" }
    }
}

