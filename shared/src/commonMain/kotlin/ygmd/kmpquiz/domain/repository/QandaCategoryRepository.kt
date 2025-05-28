package ygmd.kmpquiz.domain.repository

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.pojo.InternalQandaCategory

interface QandaCategoryRepository {
    suspend fun getCategories(): Flow<List<InternalQandaCategory>>
    suspend fun saveCategory(category: InternalQandaCategory): Result<Unit>
}