package ygmd.kmpquiz.data.repository.cron

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import ygmd.kmpquiz.domain.entities.cron.Cron
import ygmd.kmpquiz.domain.repository.CronRepository
import ygmd.kmpquiz.domain.repository.CronStorage

private val logger: Logger = Logger.withTag("InMemoryCronRepository")

class CronRepositoryImpl(
    private val storage: CronStorage
) : CronRepository {
    private val _crons = MutableStateFlow<Map<String, Cron>>(emptyMap())

    init {
        runBlocking {
            loadFromStorage()
        }
    }

    override fun observeAll(): Flow<List<Cron>> = _crons.map { it.values.toList() }

    override suspend fun getAll(): List<Cron> = _crons.value.values.toList()

    override suspend fun getById(id: String): Cron? =
        _crons.value.values.firstOrNull { it.id == id }

    override suspend fun getByQandaId(id: Long): Cron? =
        _crons.value.values.firstOrNull { it.qandaId == id }

    override suspend fun getByCategory(category: String): List<Cron> =
        _crons.value.values.filter { it.category == category }

    /* le cron global n'est associé ni à une catégory ni à un qanda id spécifique */
    override suspend fun getGlobal(): Cron? =
        _crons.value.values.firstOrNull { it.category == null && it.qandaId == null }

    override suspend fun save(cron: Cron): Result<Unit> {
        return try {
            _crons.value += (cron.id to cron)
            persist()
            Result.Companion.success(Unit)
        } catch (e: Exception){
            logger.e(e) { "Failed to save cron ${cron.id}" }
            Result.Companion.failure(e)
        }
    }

    override suspend fun delete(id: String): Result<Unit> {
        return try {
            _crons.value -= id
            persist()
            Result.Companion.success(Unit)
        } catch (e: Exception){
            logger.e(e) { "Failed to delete cron $id" }
            Result.Companion.failure(e)
        }
    }

    override suspend fun deleteByCategory(category: String): Result<Unit> {
        return try {
            _crons.value = _crons.value.filterValues { it.category != category }
            persist()
            Result.Companion.success(Unit)
        } catch (e: Exception){
            logger.e(e) { "Failed to delete crons for category $category" }
            Result.Companion.failure(e)
        }
    }

    private suspend fun persist(): Result<Unit> {
        logger.i { "Persisting ${_crons.value.size} crons" }
        return storage.save(getAll())
    }

    private suspend fun loadFromStorage(){
        logger.i { "Loading from storage" }
        try{
            _crons.value = storage.loadAll().associateBy { it.id }
        } catch (e: Exception){
            logger.e(e) { "Failed to load from storage" }
        }
    }
}