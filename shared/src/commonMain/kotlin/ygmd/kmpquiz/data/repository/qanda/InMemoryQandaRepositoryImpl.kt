package ygmd.kmpquiz.data.repository.qanda

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.error.DomainError.QandaError.NotFound
import ygmd.kmpquiz.domain.repository.DraftQanda
import ygmd.kmpquiz.domain.repository.QandaRepository

private val logger = Logger.withTag(InMemoryQandaRepository::class.simpleName.toString())

class InMemoryQandaRepository : QandaRepository {
    private val _qandasMap = MutableStateFlow<Map<Long, Qanda>>(emptyMap())
    private val qandasMap: Map<Long, Qanda>
        get() = _qandasMap.value

    override fun observeAll(): Flow<List<Qanda>> = _qandasMap.map { it.values.toList() }

    override suspend fun getAll(): List<Qanda> = _qandasMap.value.values.toList()

    override suspend fun getByCategory(category: String): List<Qanda> =
        _qandasMap.value.values
            .filter { it.metadata.category == category }

    override suspend fun save(qanda: DraftQanda): Result<Long> {
        val newId = IDGenerator.nextId
        val saved = qanda.toQanda(newId)
        _qandasMap.update { it + (newId to saved) }
        return Result.success(newId)
    }

    override suspend fun saveAll(qandas: List<DraftQanda>): Result<Unit> {
        val toAdd: Map<Long, Qanda> = qandas
                .map { draft -> draft.toQanda(IDGenerator.nextId) }
                .associateBy { it.id }

        _qandasMap.update { it + toAdd }
        return Result.success(Unit)
    }

    override suspend fun update(qanda: Qanda): Result<Unit> {
        _qandasMap.value += (qanda.id to qanda)
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
        return qandasMap.values.firstOrNull { it.contextKey == qanda.contextKey }
            ?.let { Result.success(it) } ?: Result.failure(NotFound)
    }
}

object IDGenerator {
    private var count: Long = 0
    val nextId: Long
        get() = count++
}