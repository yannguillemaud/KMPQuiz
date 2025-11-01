package ygmd.kmpquiz.data.repository.qanda

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import ygmd.kmpquiz.database.QandaEntity
import ygmd.kmpquiz.domain.dao.QandaDao
import ygmd.kmpquiz.domain.model.qanda.AnswersFactory
import ygmd.kmpquiz.domain.model.qanda.Choice
import ygmd.kmpquiz.domain.model.qanda.Qanda
import ygmd.kmpquiz.domain.model.qanda.Question
import ygmd.kmpquiz.domain.repository.QandaRepository
import ygmd.kmpquiz.domain.result.DeleteResult
import ygmd.kmpquiz.domain.result.SaveMultipleQandasResult
import ygmd.kmpquiz.domain.result.SaveQandaResult
import ygmd.kmpquiz.domain.result.UpdateResult
import java.util.UUID

private val logger = Logger.withTag(QandaRepositoryImpl::class.simpleName.toString())

class QandaRepositoryImpl(
    private val qandaDao: QandaDao,
    private val json: Json,
) : QandaRepository {
    override fun observeAll(): Flow<List<Qanda>> {
        logger.i { "Observing qandas" }
        return qandaDao.observeQandasEntity()
            .map { qandas ->
                qandas.map { mapToQanda(it) }
            }
    }

    override suspend fun getAll(): List<Qanda> {
        return qandaDao.getAll()
            .map { mapToQanda(it) }
    }

    override suspend fun getByCategory(category: String): List<Qanda> {
        return qandaDao.getByCategory(category)
            .map { mapToQanda(it) }
    }

    override suspend fun getById(id: String): Result<Qanda> {
        return qandaDao.getById(id)
            ?.let { Result.success(mapToQanda(it)) }
            ?: Result.failure(IllegalStateException("Qanda not found"))
    }

    override suspend fun getByContextKey(qanda: Qanda): Result<Qanda> {
        return qandaDao.getByContextKey(qanda.contextKey)
            ?.let { Result.success(mapToQanda(it)) }
            ?: Result.failure(IllegalStateException("Qanda not found"))
    }

    override suspend fun save(qanda: Qanda): SaveQandaResult {
        val entity = qanda.mapToEntity()
        try {
            qandaDao.save(entity)
            return SaveQandaResult.Success(qanda)
        } catch (e: Exception) {
            logger.e(e) { "Failed to save qanda" }
            return SaveQandaResult.AlreadyExists(entity.id)
        }
    }

    override suspend fun saveAll(qandas: List<Qanda>): SaveMultipleQandasResult {
        val entities = qandas.map {
            it.mapToEntity()
        }
        val alreadyExists = mutableListOf<String>()
        var successCount = 0
        entities.forEach { entity ->
            try {
                qandaDao.save(entity)
                successCount++
            } catch (e: Exception) {
                logger.e(e) { "Failed to save qanda" }
                alreadyExists += entity.id
            }
        }
        return when {
            alreadyExists.isNotEmpty() -> SaveMultipleQandasResult.AlreadyExist(
                alreadyExists.toList()
            )

            successCount == entities.size -> SaveMultipleQandasResult.Success
            else -> SaveMultipleQandasResult.GenericError(Throwable("Failed to save all qandas"))
        }
    }

    override suspend fun update(qanda: Qanda): UpdateResult {
        getById(qanda.id)
            .fold(
                onFailure = {
                    logger.e(it) { "Failed to update qanda" }
                    return UpdateResult.NotFound(qanda.id)
                },
                onSuccess = {
                    val entity = qanda.mapToEntity()
                    try {
                        qandaDao.save(entity)
                        return UpdateResult.Success
                    } catch (e: Exception) {
                        logger.e(e) { "Failed to update qanda" }
                        return UpdateResult.GenericError(e)
                    }
                }
            )
    }

    override suspend fun deleteById(id: String): DeleteResult {
        return try {
            qandaDao.deleteById(id)
            DeleteResult.Success
        } catch (e: Exception) {
            logger.e(e) { "Failed to delete qanda" }
            DeleteResult.NotFound(listOf(id))
        }
    }

    override suspend fun deleteAll(): DeleteResult {
        qandaDao.deleteAll()
        return DeleteResult.Success
    }

    override suspend fun deleteByCategory(categoryId: String): DeleteResult {
        qandaDao.deleteByCategoryId(categoryId)
        return DeleteResult.Success
    }

    private fun Qanda.mapToEntity() = QandaEntity(
        id = UUID.randomUUID().toString(),
        question_type = question.type,
        question_text = if (question is Question.TextQuestion) question.text else null,
        question_url = if (question is Question.ImageQuestion) question.imageUrl else null,
        incorrect_answers_text = answers.incorrectAnswers.map {
            when (it) {
                is Choice.ImageChoice -> it.imageUrl
                is Choice.TextChoice -> it.text
            }
        }.let { json.encodeToString(it) },
        correct_answer_text = when (answers.correctAnswer) {
            is Choice.ImageChoice -> answers.correctAnswer.imageUrl
            is Choice.TextChoice -> answers.correctAnswer.text
        },
        category_id = categoryId,
        context_key = contextKey
    )

    private fun mapToQanda(entity: QandaEntity) = Qanda(
        id = entity.id,
        question = when (Question.QuestionType.entries.firstOrNull { it.value == entity.question_type }) {
            Question.QuestionType.TEXT -> Question.TextQuestion(requireNotNull(entity.question_text))
            Question.QuestionType.IMAGE -> Question.ImageQuestion(requireNotNull(entity.question_url))
            null -> error("Invalid question type: ${entity.question_type}")
        },
        answers = AnswersFactory.createMultipleTextChoices(
            entity.correct_answer_text, json.decodeFromString(entity.incorrect_answers_text)
        ),
        categoryId = entity.category_id,
    )
}