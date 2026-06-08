package ygmd.kmpquiz.domain.repository

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.model.scheduler.SchedulerConfiguration
import ygmd.kmpquiz.domain.model.quiz.Quiz

interface QuizRepository {
    /**
     * Observe all quizzes.
     */
    fun observeAll(): Flow<List<Quiz>>

    /**
     * Get all quizzes.
     */
    suspend fun getAll(): List<Quiz>

    /**
     * Get a quiz by id.
     */
    suspend fun getById(id: String): Result<Quiz>

    /**
     * Save a quiz.
     */
    suspend fun saveQuiz(quiz: Quiz): Result<Unit>

    /**
     * Delete a quiz by id.
     */
    suspend fun deleteQuizById(id: String): Result<Unit>

    /**
     * Toggle the scheduler for a quiz.
     */
    suspend fun toggleQuizScheduler(quizId: String, newValue: Boolean): Result<Unit>

    /**
     * Get all scheduled quizzes.
     * Returns a map of quiz id to scheduler configuration.
     */
    suspend fun getAllScheduled(): Map<String, SchedulerConfiguration>
}