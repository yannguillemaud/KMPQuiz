package ygmd.kmpquiz.data.repository.QuizRepository

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ygmd.kmpquiz.domain.entities.quiz.Quiz
import ygmd.kmpquiz.domain.entities.quiz.QuizDraft
import ygmd.kmpquiz.domain.repository.QuizRepository
import java.util.UUID
import kotlin.time.ExperimentalTime

private val logger = Logger.withTag("QuizRepositoryImpl")

class QuizRepositoryImpl : QuizRepository {
    private val quizzesMap = mutableMapOf<String, Quiz>()
    private val _quizzesFlow = MutableStateFlow<List<Quiz>>(emptyList())

    override fun observeAll(): Flow<List<Quiz>> {
        return _quizzesFlow.asStateFlow()
    }

    override suspend fun getAllQuizzes(): List<Quiz> {
        return quizzesMap.values.toList()
            .sortedByDescending { it.createdAt }
    }

    override suspend fun getQuizById(id: String): Quiz? {
        return quizzesMap[id]
    }

    @ExperimentalTime
    override suspend fun insertQuiz(quiz: QuizDraft): Result<Quiz> {
        return try {
            val newQuiz = Quiz(
                id = UUID.randomUUID().toString(),
                title = quiz.title,
                qandas = quiz.qandas,
                quizCron = quiz.QuizCron
            )

            quizzesMap[newQuiz.id] = newQuiz
            updateFlow()

            logger.d { "Quiz inserted: ${newQuiz.title} (${newQuiz.id})" }
            Result.success(newQuiz)
        } catch (e: Exception) {
            logger.e(e) { "Error inserting quiz" }
            Result.failure(e)
        }
    }

    override suspend fun updateQuiz(quizId: String, transform: (Quiz) -> Quiz): Result<Unit> {
        return try {
            if (!quizzesMap.containsKey(quizId)) {
                return Result.failure(NoSuchElementException("Quiz not found: $quizId"))
            }

            val actual = quizzesMap[quizId]!!
            quizzesMap[quizId] = transform(actual)
            updateFlow()

            logger.d { "Quiz updated: $quizId" }
            Result.success(Unit)
        } catch (e: Exception) {
            logger.e(e) { "Error updating quiz: $quizId" }
            Result.failure(e)
        }
    }

    override suspend fun deleteQuizById(id: String): Result<Unit> {
        return try {
            val removed = quizzesMap.remove(id)
            if (removed == null) {
                return Result.failure(NoSuchElementException("Quiz not found: $id"))
            }

            updateFlow()
            logger.d { "Quiz deleted: $id" }
            Result.success(Unit)
        } catch (e: Exception) {
            logger.e(e) { "Error deleting quiz: $id" }
            Result.failure(e)
        }
    }

    override suspend fun deleteAll() {
        try {
            quizzesMap.clear()
            updateFlow()
            logger.d { "All quizzes deleted" }
        } catch (e: Exception) {
            logger.e(e) { "Error deleting all quizzes" }
        }
    }

    // Helper pour mettre à jour le Flow après chaque modification
    private fun updateFlow() {
        _quizzesFlow.value = quizzesMap.values
            .toList()
            .sortedByDescending { it.createdAt }
    }
}