package ygmd.kmpquiz.data.service

import ygmd.kmpquiz.domain.repository.DraftQanda

interface QandaFetcher {
    val isEnabled: Boolean
    suspend fun fetch(fetchConfig: FetchConfig): FetchResult<List<DraftQanda>>
}

data class FetchConfig(
    val amount: Int? = 20,
    val category: String? = null,
    val difficulty: String? = null,
)