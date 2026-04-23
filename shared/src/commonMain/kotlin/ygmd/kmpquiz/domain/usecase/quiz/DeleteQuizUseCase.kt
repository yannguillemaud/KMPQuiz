package ygmd.kmpquiz.domain.usecase.quiz

import ygmd.kmpquiz.domain.repository.QuizRepository
import ygmd.kmpquiz.domain.scheduler.TaskScheduler

class DeleteQuizUseCase(
    private val quizRepository: QuizRepository,
    private val taskScheduler: TaskScheduler,
) {
    suspend fun deleteQuiz(quizId: String): Result<Unit> =
        taskScheduler.updateQuizScheduling(quizId, newValue = null)
            .mapCatching {
                quizRepository.deleteQuizById(quizId)
            }
}