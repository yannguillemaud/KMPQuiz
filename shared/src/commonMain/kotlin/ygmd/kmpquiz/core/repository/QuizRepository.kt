package ygmd.kmpquiz.core.repository

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.core.domain.quiz.config.QuizSchedulerConfiguration
import ygmd.kmpquiz.core.domain.quiz.Quiz

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
    suspend fun getAllScheduled(): Map<String, QuizSchedulerConfiguration>
    fun removeCategory(categoryId: String): Result<Unit>
    fun countCategoryInUse(categoryId: String): Int

    /**
     * Returns the number of DISTINCT quizzes referencing any of the given categories.
     * Distinct so a quiz shared across several selected categories is counted once.
     */
    fun countCategoriesInUse(categoryIds: Set<String>): Int
}