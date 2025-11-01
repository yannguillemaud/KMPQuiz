package ygmd.kmpquiz.domain.repository

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.model.qanda.Qanda
import ygmd.kmpquiz.domain.result.DeleteResult
import ygmd.kmpquiz.domain.result.SaveMultipleQandasResult
import ygmd.kmpquiz.domain.result.SaveQandaResult
import ygmd.kmpquiz.domain.result.UpdateResult

interface QandaRepository {
    /* READ */
    fun observeAll(): Flow<List<Qanda>>
    suspend fun getAll(): List<Qanda>
    suspend fun getByCategory(category: String): List<Qanda>

    suspend fun getById(id: String): Result<Qanda>
    suspend fun getByContextKey(qanda: Qanda): Result<Qanda>

    /* WRITE */
    suspend fun save(qanda: Qanda): SaveQandaResult
    suspend fun saveAll(qandas: List<Qanda>): SaveMultipleQandasResult

    suspend fun update(qanda: Qanda): UpdateResult
    suspend fun deleteById(id: String): DeleteResult
    suspend fun deleteByCategory(categoryId: String): DeleteResult
    suspend fun deleteAll(): DeleteResult
}