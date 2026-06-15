package ygmd.kmpquiz.data.repository.fetcher

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import ygmd.kmpquiz.core.domain.fetcher.FetcherState
import ygmd.kmpquiz.core.domain.fetcher.QandaFetcher
import ygmd.kmpquiz.core.domain.qanda.QandaDetails
import ygmd.kmpquiz.core.repository.FetcherRepository
import ygmd.kmpquiz.core.service.fetcher.Fetcher
import java.util.UUID

class InMemoryFetcherRepository(private val fetchers: List<Fetcher>) : FetcherRepository {
    private val _fetchers: MutableStateFlow<Map<String, QandaFetcher>> = MutableStateFlow(
        fetchers.associate {
            val id = UUID.randomUUID().toString()
            id to QandaFetcher(
                id = id,
                name = it.name,
                fetcher = it,
                state = FetcherState.Idle,
            )
        }
    )

    override fun observeFetchers() = _fetchers.map { it.values.toList() }

    override suspend fun startDownload(fetcherId: String): Result<List<QandaDetails>> {
        updateState(fetcherId, FetcherState.Fetching)
        try {
            val fetcher = _fetchers.value[fetcherId]?.fetcher
                ?: return Result.failure(Exception("Fetcher not found"))
            val result = fetcher()
            updateState(
                fetcherId,
                result.fold(
                    { FetcherState.Success },
                    { FetcherState.Error(it.message ?: "Unknown error") })
            )
            return result
        } catch (e: Exception) {
            updateState(fetcherId, FetcherState.Error(e.message ?: "Unknown error"))
            return Result.failure(e)
        }
    }

    private fun updateState(fetcherId: String, state: FetcherState) {
        _fetchers.update { current ->
            current.toMutableMap().apply {
                val existing = this[fetcherId] ?: return@apply
                this[fetcherId] = existing.copy(state = state)
            }
        }
    }
}