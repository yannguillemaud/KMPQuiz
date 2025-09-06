package ygmd.kmpquiz.data.repository.fetch

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ygmd.kmpquiz.domain.repository.DraftQanda
import ygmd.kmpquiz.domain.repository.FetchRepository

class FetchRepositoryImpl : FetchRepository {
    private val _fetched = MutableStateFlow(emptyList<DraftQanda>())

    override fun observeFetched(): Flow<List<DraftQanda>> = _fetched.asStateFlow()

    override suspend fun saveDrafted(qandas: List<DraftQanda>): Result<Unit> =
        try {
            _fetched.update {
                val savedQandas = qandas.map { it.copy(isSaved = true) }
                it + savedQandas
            }
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }

    override suspend fun removeFetched(qandas: List<DraftQanda>): Result<Unit> =
        try {
            // Créer un Set des contextKeys des QandAs à supprimer pour une recherche rapide
            val contextKeysToRemove = qandas.map { it.contextKey }.toSet()

            _fetched.update { currentList ->
                // Filtrer la liste actuelle pour exclure les QandAs à supprimer
                currentList.filter { fetchedQanda ->
                    fetchedQanda.contextKey !in contextKeysToRemove
                }
            }
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }

    override suspend fun clearAllFetched(): Result<Unit> =
        try {
            _fetched.update { emptyList() }
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
}