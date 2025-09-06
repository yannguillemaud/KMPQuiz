package ygmd.kmpquiz.data.repository.quiz

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.entities.quiz.DraftQuiz
import ygmd.kmpquiz.domain.entities.quiz.Quiz
import ygmd.kmpquiz.domain.repository.QuizRepository

class PersistenceQuizRepository(
    private val dao: QuizDao,
) : QuizRepository {
    override fun observeAll(): Flow<List<Quiz>> {
        return dao.observeAllQuizzes()
    }

    override suspend fun getAllQuizzes(): List<Quiz> {
        return dao.getAllQuizzes()
    }

    override suspend fun getQuizById(id: String): Result<Quiz> {
        return dao.getQuizById(id)?.let { Result.success(it) }
            ?: Result.failure(Exception("Quiz not found"))
    }

    override suspend fun insertQuiz(draft: DraftQuiz): Result<Quiz> {
        val result = dao.insertDraft(draft)
        return result?.let {
            Result.success(
                Quiz(
                    id = it,
                    title = draft.title,
                    quizCron = draft.cron,
                    qandas = draft.qandas,
                )
            )
        } ?: Result.failure(Exception("Failed to insert quiz"))
    }

    override suspend fun updateQuiz(
        quizId: String,
        transform: Quiz.() -> Quiz
    ): Result<Unit> {
        val existing = dao.getQuizById(quizId)
        return if (existing != null) {
            dao.updateQuiz(quizId, transform(existing))
            Result.success(Unit)
        } else {
            Result.failure(Exception("Quiz not found"))
        }
    }

    override suspend fun deleteQuizById(id: String): Result<Unit> {
        try {
            dao.deleteById(id)
            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun deleteAll() {
        TODO("Not yet implemented")
    }
}