package ygmd.kmpquiz.data.repository.quiz

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ygmd.kmpquiz.database.QuizEntity
import ygmd.kmpquiz.domain.dao.QuizDao
import ygmd.kmpquiz.domain.model.cron.CronExpression
import ygmd.kmpquiz.domain.model.cron.QuizCron
import ygmd.kmpquiz.domain.model.quiz.DraftQuiz
import ygmd.kmpquiz.domain.model.quiz.Quiz
import ygmd.kmpquiz.domain.repository.QuizRepository
import ygmd.kmpquiz.domain.repository.RelationRepository
import java.util.UUID

class QuizRepositoryImpl(
    private val quizDao: QuizDao,
    private val relationRepository: RelationRepository,
) : QuizRepository {
    override fun observeAll(): Flow<List<Quiz>> {
        return quizDao.observeAllQuizzes()
            .map {
                it.map { entity ->
                    mapToQuiz(entity)
                }
            }
    }

    override suspend fun getAllQuizzes(): List<Quiz> {
        return quizDao.getAllQuizzes()
            .map { mapToQuiz(it) }
    }

    override suspend fun getQuizById(id: String): Result<Quiz> {
        return quizDao.getQuizById(id)?.let { Result.success(mapToQuiz(it)) }
            ?: Result.failure(Exception("Quiz not found"))
    }

    override suspend fun insertQuiz(draft: DraftQuiz): Result<Quiz> {
        val entity = QuizEntity(
            id = UUID.randomUUID().toString(),
            title = draft.title,
            cron_expression = draft.cron?.cron?.expression,
            cron_display_name = draft.cron?.cron?.displayName,
            cron_enabled = if (draft.cron?.isEnabled == true) 1L else 0L
        )
        val result = quizDao.save(entity)
        return result?.let {
            draft.qandas.forEach { qanda -> relationRepository.insertQandaToQuizId(it, qanda) }
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

    override suspend fun saveQuiz(
        id: String,
        quiz: Quiz
    ): Result<Unit> {
        val existing = quizDao.getQuizById(id)
        if(existing == null) {
            return Result.failure(Exception("Quiz not found"))
        }
        val entity = QuizEntity(
            id = id,
            title = quiz.title,
            cron_expression = quiz.quizCron?.cron?.expression,
            cron_display_name = quiz.quizCron?.cron?.displayName,
            cron_enabled = if (quiz.quizCron?.isEnabled == true) 1L else 0L
        )
        return quizDao.save(entity)?.let {
            val existingQandas = relationRepository.getQandasByQuizId(existing.id)
            if(existingQandas != quiz.qandas) {
                existingQandas.forEach { existingQanda ->
                    relationRepository.deleteQandaFromQuizId(existing.id, existingQanda)
                }
                quiz.qandas.forEach { qanda ->
                    relationRepository.insertQandaToQuizId(it, qanda)
                }
            }
            Result.success(Unit)
        } ?: Result.failure(Exception("Failed to update quiz"))
    }

    override suspend fun deleteQuizById(id: String): Result<Unit> {
        try {
            quizDao.deleteById(id)
            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun deleteAll() {
        TODO("Not yet implemented")
    }

    private suspend fun mapToQuiz(entity: QuizEntity): Quiz = Quiz(
        id = entity.id,
        title = entity.title,
        quizCron = entity.cron_expression?.let {
            QuizCron(
                cron = CronExpression(it, displayName = requireNotNull(entity.cron_display_name)),
                isEnabled = entity.cron_enabled == 1L
            )
        },
        qandas = relationRepository.getQandasByQuizId(entity.id)
    )
}