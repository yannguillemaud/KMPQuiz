package ygmd.kmpquiz.domain.usecase.notification

import co.touchlab.kermit.Logger
import ygmd.kmpquiz.domain.scheduler.TaskScheduler
import ygmd.kmpquiz.domain.repository.QuizRepository

private val logger = Logger.withTag("RescheduleTasksUseCase")

class RescheduleTasksUseCase(
    private val taskScheduler: TaskScheduler,
    private val quizRepository: QuizRepository,
) {
    suspend fun execute(){
        logger.i { "Executing rescheduler" }
        taskScheduler.rescheduleQuizReminders(quizRepository.getAllQuizzes())
    }
}