package ygmd.kmpquiz.domain.repository.qanda

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.pojo.InternalQanda

interface QandaRepository {
    fun getAll(): Flow<List<InternalQanda>>
    suspend fun findById(id: Long): Either<QandaOperationError, InternalQanda>
    suspend fun existsByContentKey(qanda: InternalQanda): Either<QandaOperationError, InternalQanda>
    suspend fun save(qanda: InternalQanda): Either<QandaOperationError, Long>
    suspend fun update(qanda: InternalQanda): Either<QandaOperationError, Unit>
    suspend fun saveAll(qandas: List<InternalQanda>): Either<QandaOperationError, Unit>
    suspend fun deleteById(id: Long): Either<QandaOperationError, Unit>
    suspend fun deleteAll()
}