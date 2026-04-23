package ygmd.kmpquiz.domain.usecase.notification

import co.touchlab.kermit.Logger
import ygmd.kmpquiz.domain.repository.QuizRepository
import ygmd.kmpquiz.domain.scheduler.TaskScheduler

private val logger = Logger.withTag("RescheduleTasksUseCase")

class ScheduleAllQuizzesUseCase(
    private val taskScheduler: TaskScheduler,
    private val quizRepository: QuizRepository,
) {
    suspend operator fun invoke() {
        logger.d { "Executing rescheduler for all quizzes" }
        val quizzes = quizRepository.getAllQuizzes()
        taskScheduler.scheduleAllQuizzes(quizzes)
    }
}