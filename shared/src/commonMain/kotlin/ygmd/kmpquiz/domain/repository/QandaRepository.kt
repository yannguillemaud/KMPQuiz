package ygmd.kmpquiz.domain.repository

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.pojo.InternalQanda

interface QandaRepository {
    fun observeQandas(): Flow<List<InternalQanda>>
    suspend fun getAll(): List<InternalQanda>
    suspend fun saveAll(qandas: List<InternalQanda>)
    suspend fun save(qanda: InternalQanda)
    suspend fun findById(id: Long): InternalQanda?
    suspend fun deleteById(id: Long): Boolean
    suspend fun deleteAll(id: Long): Boolean
}