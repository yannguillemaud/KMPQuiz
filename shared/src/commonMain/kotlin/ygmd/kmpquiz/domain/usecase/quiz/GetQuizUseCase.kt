package ygmd.kmpquiz.domain.usecase.quiz

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.model.quiz.Quiz
import ygmd.kmpquiz.domain.repository.QuizRepository

class GetQuizUseCase(
    private val quizRepository: QuizRepository,
) {
    fun observeQuizzes(): Flow<List<Quiz>> = quizRepository.observeAll()
    suspend fun getAll(): List<Quiz> = quizRepository.getAll()
    suspend fun getById(quizId: String): Quiz? = quizRepository.getById(quizId).getOrNull()
}