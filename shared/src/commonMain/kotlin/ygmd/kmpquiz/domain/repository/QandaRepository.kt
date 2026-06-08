package ygmd.kmpquiz.domain.repository

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.model.qanda.Qanda

interface QandaRepository {
    /* READ */
    fun observeAll(): Flow<List<Qanda>>
    suspend fun getAll(): List<Qanda>
    // todo - should be result<list> ?
    suspend fun getByCategory(category: String): List<Qanda>

    suspend fun getById(id: String): Qanda?
    suspend fun getByContextKey(qanda: Qanda): Qanda?

    /* WRITE */
    suspend fun save(qanda: Qanda): Result<Unit>
    suspend fun deleteById(id: String): Result<Unit>
    suspend fun deleteByCategory(categoryId: String): Result<Unit>
    suspend fun deleteAll()
}