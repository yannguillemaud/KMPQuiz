package ygmd.kmpquiz.data.repository.fetch

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import ygmd.kmpquiz.domain.repository.DraftQanda
import ygmd.kmpquiz.domain.repository.FetchRepository

class FetchRepositoryImpl : FetchRepository {
    private val _fetched = MutableStateFlow(emptySet<DraftQanda>())

    override fun observeFetched(): Flow<List<DraftQanda>> = _fetched.asStateFlow().map { it.toList() }

    override suspend fun saveDrafted(qandas: List<DraftQanda>): Result<Unit> =
        try {
            _fetched.update {
                it + qandas
            }
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }

    override suspend fun removeFetched(qandas: List<DraftQanda>): Result<Unit> =
        try {
            val contextKeysToRemove = qandas.map { it.contextKey }.toSet()
            _fetched.update { current ->
                current.filter { fetchedQanda ->
                    fetchedQanda.contextKey !in contextKeysToRemove
                }.toSet()
            }
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }

    override suspend fun clearAllFetched(): Result<Unit> =
        try {
            _fetched.update { emptySet() }
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
}