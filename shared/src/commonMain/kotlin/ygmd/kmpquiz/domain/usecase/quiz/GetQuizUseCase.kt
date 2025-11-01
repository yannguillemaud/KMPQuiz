package ygmd.kmpquiz.domain.usecase.quiz

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.model.quiz.Quiz
import ygmd.kmpquiz.domain.repository.QuizRepository

class GetQuizUseCase(
    private val quizRepository: QuizRepository,
) {
    fun observeAll(): Flow<List<Quiz>> = quizRepository.observeAll()
    suspend fun getQuizById(id: String): Result<Quiz> = quizRepository.getQuizById(id)
}