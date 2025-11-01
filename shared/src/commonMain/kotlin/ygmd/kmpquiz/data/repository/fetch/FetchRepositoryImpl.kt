package ygmd.kmpquiz.data.repository.fetch

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ygmd.kmpquiz.domain.model.draftqanda.DraftQanda
import ygmd.kmpquiz.domain.model.fetcher.QandaFetcher
import ygmd.kmpquiz.domain.repository.FetchRepository
import ygmd.kmpquiz.domain.result.FetchResult
import ygmd.kmpquiz.domain.service.Fetcher

class FetchRepositoryImpl(
    private val fetchers: List<Fetcher>
) : FetchRepository {
    private val _fetchers = MutableStateFlow(
        fetchers.map {
            QandaFetcher(
                id = it.id,
                name = it.name,
                isEnabled = it.isEnabled,
                fetcher = it
            )
        }
    )

    override fun observeFetchers(): Flow<List<QandaFetcher>> = _fetchers.asStateFlow()

    override fun getFetcherById(fetcherId: String): Result<QandaFetcher> =
        fetchers.firstOrNull { it.id == fetcherId }
            ?.let {
                Result.success(
                    QandaFetcher(
                        it.id,
                        it.name,
                        it.isEnabled,
                        it
                    )
                )
            }
            ?: Result.failure(IllegalArgumentException("Fetcher with id $fetcherId not found"))

    override suspend fun fetch(qandaFetcher: QandaFetcher): FetchResult<List<DraftQanda>> {
        return qandaFetcher.fetcher.fetch()
    }

    override fun updateFetcher(
        fetcherId: String,
        qandaFetcher: QandaFetcher
    ) {
        _fetchers.value = _fetchers.value.map {
            if (it.id == fetcherId) qandaFetcher else it
        }
    }
}