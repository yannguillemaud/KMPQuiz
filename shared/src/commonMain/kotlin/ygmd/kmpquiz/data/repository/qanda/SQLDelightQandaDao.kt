package ygmd.kmpquiz.data.repository.qanda

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import ygmd.kmpquiz.database.KMPQuizDatabase
import ygmd.kmpquiz.database.Qanda_entity
import ygmd.kmpquiz.data.dao.QandaDao


private val logger = Logger.withTag("PersistenceMemoryDao")

class SQLDelightQandaDao(
    database: KMPQuizDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : QandaDao {
    private val qandaQueries = database.qandaQueries

    override fun observeQandasEntity(): Flow<List<Qanda_entity>> {
        logger.i { "Observing all qandas with categories" }
        return qandaQueries.selectAll()
            .asFlow()
            .mapToList(dispatcher)
    }

    override suspend fun getAll(): List<Qanda_entity> {
        return withContext(dispatcher) {
            qandaQueries.selectAll().executeAsList()
        }
    }

    override suspend fun getById(id: String): Qanda_entity? {
        return withContext(dispatcher) {
            qandaQueries.getById(id).executeAsOneOrNull()
        }
    }

    override suspend fun getByCategory(categoryId: String): List<Qanda_entity> {
        return withContext(dispatcher) {
            qandaQueries.getByCategory(categoryId).executeAsList()
        }
    }

    override suspend fun getByContextKey(contextKey: String): Qanda_entity? {
        return withContext(dispatcher) {
            qandaQueries.getByContextKey(contextKey).executeAsOneOrNull()
        }
    }

    override suspend fun save(entity: Qanda_entity): Qanda_entity {
        withContext(dispatcher) {
            qandaQueries.saveQanda(
                id = entity.id,
                question = entity.question,
                answers = entity.answers,
                category_id = entity.category_id,
                context_key = entity.context_key
            )
        }
        return entity
    }

    override suspend fun deleteAll() {
        withContext(dispatcher) {
            qandaQueries.deleteAll()
        }
    }

    override suspend fun deleteById(id: String) {
        withContext(dispatcher) {
            qandaQueries.deleteById(id)
        }
    }

    override suspend fun deleteByCategoryId(categoryId: String) {
        withContext(dispatcher) {
            qandaQueries.deleteByCategoryId(categoryId)
        }
    }
}