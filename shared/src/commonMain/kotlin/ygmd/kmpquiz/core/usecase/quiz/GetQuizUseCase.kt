package ygmd.kmpquiz.core.usecase.quiz

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.core.domain.quiz.Quiz
import ygmd.kmpquiz.core.repository.QuizRepository

class GetQuizUseCase(
    private val quizRepository: QuizRepository,
) {
    fun observeQuizzes(): Flow<List<Quiz>> = quizRepository.observeAll()
    suspend fun getAll(): List<Quiz> = quizRepository.getAll()
    suspend fun getById(quizId: String): Quiz? = quizRepository.getById(quizId).getOrNull()
}