package ygmd.kmpquiz.data.repository.qanda

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.dao.QandaDao
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.repository.DraftQanda
import ygmd.kmpquiz.domain.repository.QandaRepository

private val logger = Logger.withTag(QandaRepositoryImpl::class.simpleName.toString())

class QandaRepositoryImpl(
    private val qandaDao: QandaDao
) : QandaRepository {
    override fun observeAll(): Flow<List<Qanda>> {
        logger.i { "Observing qandas" }
        return qandaDao.observeQandas()
    }

    override suspend fun getAll(): List<Qanda> {
        return qandaDao.getAllQandas()
    }

    override suspend fun getByCategory(category: String): List<Qanda> {
        return qandaDao.getQandasByCategory(category)
    }

    override suspend fun findById(id: String): Result<Qanda> {
        return qandaDao.getQandaById(id)
            ?.let { Result.success(it) }
            ?: Result.failure(IllegalStateException("Qanda not found"))
    }

    override suspend fun findByContentKey(qanda: Qanda): Result<Qanda> {
        return qandaDao.getQandaByContextKey(qanda.contextKey)
            ?.let { Result.success(it) }
            ?: Result.failure(IllegalStateException("Qanda not found"))
    }

    override suspend fun save(qanda: DraftQanda): Result<String> {
        return try {
            val id = qandaDao.saveDraft(qanda)
            Result.success(id)
        } catch (e: Exception) {
            logger.e(e) { "Failed to save qanda" }
            Result.failure(e)
        }
    }

    override suspend fun update(qanda: Qanda): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun saveAll(qandas: List<DraftQanda>): Result<Unit> {
        return try {
            qandaDao.saveAllDraft(qandas)
            Result.success(Unit)
        } catch (e: Exception) {
            logger.e(e) { "Failed to save qandas" }
            Result.failure(e)
        }
    }

    override suspend fun deleteById(id: String): Result<Unit> {
        return try {
            qandaDao.deleteQandaById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            logger.e(e) { "Failed to delete qanda" }
            Result.failure(e)
        }
    }

    override suspend fun deleteAll(): Result<Unit> {
        return try {
            qandaDao.deleteAllQandas()
            Result.success(Unit)
        } catch (e: Exception) {
            logger.e(e) { "Failed to delete all qandas" }
            Result.failure(e)
        }
    }
}

