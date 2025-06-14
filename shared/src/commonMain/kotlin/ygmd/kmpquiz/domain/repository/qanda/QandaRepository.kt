package ygmd.kmpquiz.domain.repository.qanda

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.pojo.qanda.InternalQanda

interface QandaRepository {
    fun getAll(): Flow<List<InternalQanda>>
    suspend fun findById(id: Long): Result<InternalQanda>
    suspend fun findByContentKey(qanda: InternalQanda): Result<InternalQanda>
    suspend fun save(qanda: InternalQanda): Result<Long>
    suspend fun update(qanda: InternalQanda): Result<Unit>
    suspend fun saveAll(qandas: List<InternalQanda>): Result<Unit>
    suspend fun deleteById(id: Long): Result<Unit>
    suspend fun deleteAll(): Result<Unit>
}