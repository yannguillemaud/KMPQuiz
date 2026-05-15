package ygmd.kmpquiz.domain.usecase.quiz

import ygmd.kmpquiz.domain.repository.QuizRepository
import ygmd.kmpquiz.domain.scheduler.QuizScheduler

class DeleteQuizUseCase(
    private val quizRepository: QuizRepository,
    private val quizScheduler: QuizScheduler,
) {
    suspend fun deleteQuiz(quizId: String): Result<Unit> {
        return quizRepository.deleteQuizById(quizId)
            .onSuccess { quizScheduler.cancelAlarm(quizId) }
    }
}