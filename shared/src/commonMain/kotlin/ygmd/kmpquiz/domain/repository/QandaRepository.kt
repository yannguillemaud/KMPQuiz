package ygmd.kmpquiz.domain.repository

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.pojo.InternalQanda

interface QandaRepository {
    suspend fun getAll(): Flow<Either<OperationError, List<InternalQanda>>>
    suspend fun findById(id: Long): Either<OperationError, InternalQanda>
    suspend fun existsByContentKey(qanda: InternalQanda): Either<OperationError, InternalQanda>
    suspend fun save(qanda: InternalQanda): Either<OperationError, Long>
    suspend fun update(qanda: InternalQanda): Either<OperationError, Unit>
    suspend fun saveAll(qandas: List<InternalQanda>): Either<OperationError, Unit>
    suspend fun deleteById(id: Long): Either<OperationError, Unit>
    suspend fun deleteAll()
}