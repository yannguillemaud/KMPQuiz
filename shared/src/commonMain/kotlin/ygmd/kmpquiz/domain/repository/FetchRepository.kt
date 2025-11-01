package ygmd.kmpquiz.domain.repository

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.model.draftqanda.DraftQanda
import ygmd.kmpquiz.domain.model.fetcher.QandaFetcher
import ygmd.kmpquiz.domain.result.FetchResult

interface FetchRepository {
    fun observeFetchers(): Flow<List<QandaFetcher>>
    fun getFetcherById(fetcherId: String): Result<QandaFetcher>
    suspend fun fetch(qandaFetcher: QandaFetcher): FetchResult<List<DraftQanda>>
    fun updateFetcher(fetcherId: String, qandaFetcher: QandaFetcher)
}