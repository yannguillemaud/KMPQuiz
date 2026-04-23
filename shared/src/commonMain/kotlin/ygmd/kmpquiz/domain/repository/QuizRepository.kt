package ygmd.kmpquiz.domain.repository

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.model.quiz.Quiz

interface QuizRepository {
    fun observeAll(): Flow<List<Quiz>>

    suspend fun getAllQuizzes(): List<Quiz>
    suspend fun getQuizById(id: String): Result<Quiz>
    suspend fun saveQuiz(quiz: Quiz): Result<Unit>
    suspend fun deleteQuizById(id: String): Result<Unit>
    suspend fun toggleCron(quizId: String, newValue: Boolean): Result<Unit>
}