package ygmd.kmpquiz.domain.repository

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.entities.qanda.Qanda

interface QandaRepository {
    fun observeAll(): Flow<List<Qanda>>
    suspend fun getAll(): List<Qanda>
    suspend fun getByCategory(category: String): List<Qanda>
    suspend fun findById(id: Long): Result<Qanda>
    suspend fun findByContentKey(qanda: Qanda): Result<Qanda>
    suspend fun save(qanda: DraftQanda): Result<Long>
    suspend fun update(qanda: Qanda): Result<Unit>
    suspend fun saveAll(qandas: List<DraftQanda>): Result<Unit>
    suspend fun deleteById(id: Long): Result<Unit>
    suspend fun deleteAll(): Result<Unit>
}