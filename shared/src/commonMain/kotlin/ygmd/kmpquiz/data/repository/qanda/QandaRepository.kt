package ygmd.kmpquiz.data.repository.qanda

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.entities.qanda.Qanda

interface QandaRepository {
    fun getAll(): Flow<List<Qanda>>
    suspend fun findById(id: Long): Result<Qanda>
    suspend fun findByContentKey(qanda: Qanda): Result<Qanda>
    suspend fun save(qanda: Qanda): Result<Long>
    suspend fun update(qanda: Qanda): Result<Unit>
    suspend fun saveAll(qandas: List<Qanda>): Result<Unit>
    suspend fun deleteById(id: Long): Result<Unit>
    suspend fun deleteAll(): Result<Unit>
}