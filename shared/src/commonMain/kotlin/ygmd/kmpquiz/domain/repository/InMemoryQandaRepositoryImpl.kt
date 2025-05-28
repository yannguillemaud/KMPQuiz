package ygmd.kmpquiz.domain.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ygmd.kmpquiz.domain.pojo.InternalQanda
import ygmd.kmpquiz.domain.pojo.contentKey
import ygmd.kmpquiz.domain.repository.OperationError.AlreadyExists
import ygmd.kmpquiz.domain.repository.OperationError.NotFound

sealed interface OperationError {
    data class Error(val message: String) : OperationError
    data object AlreadyExists : OperationError
    data object NotFound : OperationError
}

class InMemoryQandaRepository : QandaRepository {
    private val qandasMap: MutableMap<Long, InternalQanda> = mutableMapOf()

    override suspend fun getAll(): Flow<Either<OperationError, List<InternalQanda>>> =
        flow { emit(qandasMap.values.toList().right()) }

    override suspend fun save(qanda: InternalQanda): Either<OperationError, Long> {
        if (qanda.id != null) {
            println("WARINING: qanda already has an id:: $qanda")
            if (findById(qanda.id).isRight()) {
                println("ERROR: already exists for id:: ${qanda.id}")
                return AlreadyExists.left()
            }
        }

        val newId = IDGenerator.nextId
        qandasMap[newId] = (qanda.copy(id = newId))
        return newId.right()
    }

    override suspend fun saveAll(qandas: List<InternalQanda>): Either<OperationError, Unit> {
        val conflict = qandas.any { it.id != null && qandasMap.contains(it.id) }
        if (conflict) return AlreadyExists.left()

        qandas.forEach {
            val nextId = IDGenerator.nextId
            qandasMap[nextId] = it.copy(id = nextId)
        }

        return Unit.right()
    }

    override suspend fun update(qanda: InternalQanda): Either<OperationError, Unit> {
        val id = qanda.id ?: return OperationError.Error("Cannot update Qanda with null ID").left()

        return if (qandasMap.containsKey(id)) {
            qandasMap[id] = qanda
            Unit.right()
        } else NotFound.left()
    }

    override suspend fun deleteById(id: Long): Either<OperationError, Unit> {
        return if (qandasMap.remove(id) != null) Unit.right()
        else NotFound.left()
    }

    override suspend fun deleteAll() {
        qandasMap.clear()
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