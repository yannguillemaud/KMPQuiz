package ygmd.kmpquiz.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import ygmd.kmpquiz.core.domain.quiz.config.QuizSchedulerConfiguration
import ygmd.kmpquiz.core.domain.quiz.Quiz
import ygmd.kmpquiz.core.repository.QuizRepository
import java.util.Collections.emptyMap

class FakeQuizRepository: QuizRepository {
    private val quizzes = MutableStateFlow<Map<String, Quiz>>(emptyMap())

    var shouldSaveFail = false

    override fun observeAll(): Flow<List<Quiz>> {
        return quizzes.map { it.values.toList() }
    }

    override suspend fun getAll(): List<Quiz> {
        return quizzes.value.values.toList()
    }

    override suspend fun getById(id: String): Result<Quiz> {
        return quizzes.value[id]?.let { Result.success(it) } ?: Result.failure(Exception("Quiz not found"))
    }

    override suspend fun saveQuiz(quiz: Quiz): Result<Unit> {
        if(shouldSaveFail) return Result.failure(Exception("Save failed"))
        quizzes.value = quizzes.value.toMutableMap().apply { put(quiz.id, quiz) }
        return Result.success(Unit)
    }

    override suspend fun deleteQuizById(id: String): Result<Unit> {
        quizzes.value = quizzes.value.toMutableMap().apply { remove(id) }
        return Result.success(Unit)
    }

    override suspend fun toggleQuizScheduler(
        quizId: String,
        newValue: Boolean
    ): Result<Unit> {
        quizzes.value = quizzes.value.toMutableMap().apply {
            this[quizId]?.let {
                put(quizId, it.copy(schedulerConfiguration = it.schedulerConfiguration?.copy(isEnabled = newValue)))
            }
        }
        return Result.success(Unit)
    }

    override suspend fun getAllScheduled(): Map<String, QuizSchedulerConfiguration> {
        return quizzes.value.values.filter { it.schedulerConfiguration?.isEnabled == true }
            .associate { it.id to it.schedulerConfiguration!! }
    }
}