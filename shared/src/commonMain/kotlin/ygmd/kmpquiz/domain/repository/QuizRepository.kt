package ygmd.kmpquiz.domain.repository

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.entities.quiz.DraftQuiz
import ygmd.kmpquiz.domain.entities.quiz.Quiz

interface QuizRepository {
    fun observeAll(): Flow<List<Quiz>>

    suspend fun getAllQuizzes(): List<Quiz>
    suspend fun getQuizById(id: String): Result<Quiz>

    suspend fun insertQuiz(draft: DraftQuiz): Result<Quiz>
    suspend fun updateQuiz(quizId: String, transform: Quiz.() -> Quiz): Result<Unit>

    suspend fun deleteQuizById(id: String): Result<Unit>
    suspend fun deleteAll()
}