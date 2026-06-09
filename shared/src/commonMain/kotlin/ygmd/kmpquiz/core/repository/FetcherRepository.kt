package ygmd.kmpquiz.core.repository

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.core.domain.fetcher.QandaFetcher
import ygmd.kmpquiz.core.domain.qanda.QandaDetails
import ygmd.kmpquiz.core.service.fetcher.Fetcher

interface FetcherRepository {
    fun observeFetchers(): Flow<List<QandaFetcher>>
    suspend fun startDownload(fetcherId: String): Result<List<QandaDetails>>
}