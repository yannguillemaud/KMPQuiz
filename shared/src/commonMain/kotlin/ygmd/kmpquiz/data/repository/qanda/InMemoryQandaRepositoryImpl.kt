package ygmd.kmpquiz.data.repository.qanda

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.error.DomainError.PersistenceError.DatabaseError
import ygmd.kmpquiz.domain.error.DomainError.QandaError.NotFound

val logger = Logger.withTag(InMemoryQandaRepository::class.simpleName.toString())

class InMemoryQandaRepository : QandaRepository {
    private val _qandasMap = MutableStateFlow<Map<Long, Qanda>>(emptyMap())
    private val qandasMap: Map<Long, Qanda>
        get() = _qandasMap.value

    override fun getAll(): Flow<List<Qanda>> =
        _qandasMap.map { it.values.toList() }

    override suspend fun save(qanda: Qanda): Result<Long> {
        if (qanda.id != null) {
            logger.w { "Qanda already has id: ${qanda.id}" }
        }

        val newId = IDGenerator.nextId
        val savedQanda = qanda.copy(id = newId)
        _qandasMap.value += (newId to savedQanda)
        return Result.success(newId)
    }

    override suspend fun saveAll(qandas: List<Qanda>): Result<Unit> {
        val conflicts = qandas.mapNotNull { it.id }
        if (conflicts.isNotEmpty()) {
            logger.w { "Following qandas already have ids: $conflicts" }
        }

        val newEntries = qandas.associate { qanda ->
            val newId = IDGenerator.nextId
            newId to qanda.copy(id = newId)
        }
        _qandasMap.value += newEntries
        return Result.success(Unit)
    }

    override suspend fun update(qanda: Qanda): Result<Unit> {
        val id = qanda.id ?: return Result.failure(
            DatabaseError("Cannot update Qanda with null ID")
        )

        _qandasMap.value += (id to qanda)
        return Result.success(Unit)
    }

    override suspend fun deleteById(id: Long): Result<Unit> {
        val toRemove = qandasMap[id]
        return if (toRemove != null) {
            _qandasMap.value -= id
            Result.success(Unit)
        } else Result.failure(NotFound)
    }

    override suspend fun deleteAll(): Result<Unit> {
        _qandasMap.value = emptyMap()
        return Result.success(Unit)
    }

    override suspend fun findById(id: Long): Result<Qanda> =
        qandasMap[id]?.let { Result.success(it) } ?: Result.failure(NotFound)

    override suspend fun findByContentKey(qanda: Qanda): Result<Qanda> {
        return qandasMap.values
            .firstOrNull { it.contextKey == qanda.contextKey }
            ?.let { Result.success(it) } ?: Result.failure(NotFound)
    }
}

object IDGenerator {
    private var count: Long = 0
    val nextId: Long
        get() = count++
}