package ygmd.kmpquiz.core.usecase.quiz

import ygmd.kmpquiz.core.repository.QuizRepository
import ygmd.kmpquiz.core.service.scheduler.QuizAlarmScheduler

class DeleteQuizUseCase(
    private val quizRepository: QuizRepository,
    private val quizAlarmScheduler: QuizAlarmScheduler,
) {
    suspend fun deleteQuiz(quizId: String): Result<Unit> {
        return quizRepository.deleteQuizById(quizId)
            .onSuccess { quizAlarmScheduler.cancelAlarm(quizId) }
    }
}