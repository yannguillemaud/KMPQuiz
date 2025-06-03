package ygmd.kmpquiz.domain.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import ygmd.kmpquiz.domain.pojo.InternalQanda
import ygmd.kmpquiz.domain.pojo.contentKey
import ygmd.kmpquiz.domain.repository.OperationError.AlreadyExists
import ygmd.kmpquiz.domain.repository.OperationError.NotFound

val logger = Logger.withTag(InMemoryQandaRepository::class.simpleName.toString())

sealed class OperationError(val message: String) {
    data class Error(val errorMessage: String) : OperationError(errorMessage)
    data object AlreadyExists : OperationError("Already exists")
    data object NotFound : OperationError("Not Found")
}

class InMemoryQandaRepository : QandaRepository {
    private val _qandasMap = MutableStateFlow<Map<Long, InternalQanda>>(emptyMap())
    private val qandasMap: Map<Long, InternalQanda>
        get() = _qandasMap.value

    override fun getAll(): Flow<List<InternalQanda>> =
        _qandasMap.map { it.values.toList() }

    override suspend fun save(qanda: InternalQanda): Either<OperationError, Long> {
        try {
            if (qanda.id != null) {
                logger.w { "Qanda already has id: ${qanda.id}" }
                val contentKey = qanda.contentKey()
                if (qandasMap.filterValues { it.contentKey() == contentKey }.isNotEmpty()) {
                    logger.w { "Qanda already exists by content key: $contentKey" }
                }
                if (qandasMap.containsKey(qanda.id)) {
                    return AlreadyExists.left()
                }
            }

            val newId = IDGenerator.nextId
            val savedQanda = qanda.copy(id = newId)
            _qandasMap.value += (newId to savedQanda)
            return newId.right()
        } catch (e: Exception) {
            val message = e.message ?: "Unknown error"
            return OperationError.Error(message).left()
        }
    }

    override suspend fun saveAll(qandas: List<InternalQanda>): Either<OperationError, Unit> {
        try {
            val conflicts = qandas.filter { it.id != null && qandasMap.containsKey(it.id) }
            if (conflicts.isNotEmpty()) {
                logger.e { conflicts.joinToString(prefix = "Already exists: [", postfix = "]") }
                return AlreadyExists.left()
            }

            val newEntries = qandas.associateBy { IDGenerator.nextId }
            _qandasMap.value += newEntries
            return Unit.right()
        } catch (e: Exception) {
            val message = e.message ?: "Unknown error"
            return OperationError.Error(message).left()
        }
    }

    override suspend fun update(qanda: InternalQanda): Either<OperationError, Unit> {
        val id = qanda.id ?: return OperationError.Error("Cannot update Qanda with null ID").left()

        return if (qandasMap.containsKey(id)) {
            _qandasMap.value += (id to qanda)
            Unit.right()
        } else NotFound.left()
    }

    override suspend fun deleteById(id: Long): Either<OperationError, Unit> {
        val toRemove = qandasMap[id]
        return if (toRemove != null) {
            _qandasMap.value -= id
            Unit.right()
        } else NotFound.left()
    }

    override suspend fun deleteAll() {
        _qandasMap.value = emptyMap()
    }

    override suspend fun findById(id: Long): Either<OperationError, InternalQanda> {
        return qandasMap[id]?.right() ?: NotFound.left()
    }

    override suspend fun existsByContentKey(qanda: InternalQanda): Either<OperationError, InternalQanda> {
        return qandasMap.values
            .firstOrNull { it.contentKey() == qanda.contentKey() }
            ?.right() ?: NotFound.left()
    }
}

object IDGenerator {
    private var count: Long = 0
    val nextId: Long
        get() = count++
}