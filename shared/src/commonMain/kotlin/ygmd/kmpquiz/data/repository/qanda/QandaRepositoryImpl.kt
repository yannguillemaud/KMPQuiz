package ygmd.kmpquiz.data.repository.qanda

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ygmd.kmpquiz.database.Qanda_entity
import ygmd.kmpquiz.domain.dao.QandaDao
import ygmd.kmpquiz.domain.model.qanda.Qanda
import ygmd.kmpquiz.domain.repository.QandaRepository

private val logger = Logger.withTag("QandaRepository")

class QandaRepositoryImpl(private val qandaDao: QandaDao) : QandaRepository {
    override fun observeAll(): Flow<List<Qanda>> = qandaDao.observeQandasEntity()
        .map { qandas -> qandas.map { it.toQanda() } }

    override suspend fun getAll(): List<Qanda> = qandaDao.getAll()
        .map { it.toQanda() }

    override suspend fun getByCategory(category: String): List<Qanda> =
        qandaDao.getByCategory(category).map { it.toQanda() }

    override suspend fun getById(id: String): Qanda? = qandaDao.getById(id)?.toQanda()

    override suspend fun getByContextKey(qanda: Qanda): Qanda? =
        qandaDao.getByContextKey(qanda.contextKey)?.toQanda()

    override suspend fun save(qanda: Qanda): Result<Unit> = try {
        val entity = Qanda_entity(
            id = qanda.id,
            question = qanda.question,
            answers = qanda.answers,
            category_id = qanda.categoryId,
            context_key = qanda.contextKey,
        )
        qandaDao.save(entity)
        Result.success(Unit)
    } catch (e: Exception) {
        logger.e(e) { "Failed to save qanda" }
        Result.failure(e)
    }

    override suspend fun deleteById(id: String): Result<Unit> = try {
        qandaDao.deleteById(id)
        Result.success(Unit)
    } catch (e: Exception) {
        logger.e(e) { "Failed to delete qanda $id" }
        Result.failure(e)
    }

    override suspend fun deleteByCategory(categoryId: String): Result<Unit> = try {
        qandaDao.deleteByCategoryId(categoryId)
        Result.success(Unit)
    } catch (e: Exception) {
        logger.e(e) { "Failed to delete qandas for category $categoryId" }
        Result.failure(e)
    }

    override suspend fun deleteAll() = qandaDao.deleteAll()
}

private fun Qanda_entity.toQanda() = Qanda(
    id = id,
    question = question,
    answers = answers,
    categoryId = category_id,
)