package ygmd.kmpquiz.domain.usecase.quiz

import ygmd.kmpquiz.domain.repository.QuizRepository

class DeleteQuizUseCase(
    private val quizRepository: QuizRepository
) {
    suspend fun deleteQuiz(quizId: String): Result<Unit> {
        return quizRepository.deleteQuizById(quizId)
    }
}