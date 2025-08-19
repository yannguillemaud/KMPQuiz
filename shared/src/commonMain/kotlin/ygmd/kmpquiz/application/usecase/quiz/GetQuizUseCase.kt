package ygmd.kmpquiz.application.usecase.quiz

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.entities.quiz.Quiz
import ygmd.kmpquiz.domain.repository.QuizRepository

class GetQuizUseCase(
    private val quizRepository: QuizRepository,
) {
    fun observeAll(): Flow<List<Quiz>> = quizRepository.observeAll()
    suspend fun getAllQuizz(): List<Quiz> = quizRepository.getAllQuizzes()
    suspend fun getQuizById(id: String): Quiz? = quizRepository.getQuizById(id)
}