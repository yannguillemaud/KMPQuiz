package ygmd.kmpquiz.data.service

import ygmd.kmpquiz.data.repository.service.FetchResult
import ygmd.kmpquiz.domain.entities.qanda.Qanda

interface QandaFetcher {
    val isEnabled: Boolean
    suspend fun fetch(fetchConfig: FetchConfig): FetchResult<List<Qanda>>
}

data class FetchConfig(
    val amount: Int? = 20,
    val category: String? = null,
    val difficulty: String? = null,
)