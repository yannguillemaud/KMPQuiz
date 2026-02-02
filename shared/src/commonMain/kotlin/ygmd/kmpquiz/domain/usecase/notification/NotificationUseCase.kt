package ygmd.kmpquiz.domain.usecase.notification

import co.touchlab.kermit.Logger
import ygmd.kmpquiz.domain.repository.QuizRepository
import ygmd.kmpquiz.domain.scheduler.TaskScheduler

private val logger = Logger.withTag("RescheduleTasksUseCase")

class RescheduleTasksUseCase(
    private val taskScheduler: TaskScheduler,
    private val quizRepository: QuizRepository,
) {
    suspend fun rescheduleAll() {
        logger.i { "Executing rescheduler" }
        val quizzes = quizRepository.getAllQuizzes()
        taskScheduler.rescheduleAllQuizzes(quizzes)
    }

    suspend fun rescheduleQuiz(quizId: String){
        logger.i { "Executing rescheduler for quiz $quizId" }
        quizRepository.getQuizById(quizId)
            .fold(
                onSuccess = { taskScheduler.rescheduleQuiz(it.id, it.quizCron) },
                onFailure = { logger.e(it) { "Error getting quiz $quizId" } }
            )
    }
}