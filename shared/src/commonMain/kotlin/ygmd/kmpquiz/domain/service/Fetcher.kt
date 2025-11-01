package ygmd.kmpquiz.domain.service

import ygmd.kmpquiz.domain.model.draftqanda.DraftQanda
import ygmd.kmpquiz.domain.result.FetchResult

interface Fetcher {
    val id: String
    val name: String
    val isEnabled: Boolean get() = true
    suspend fun fetch(): FetchResult<List<DraftQanda>>
}

data class FetchConfig(
    val amount: Int? = 20,
    val category: String? = null,
    val difficulty: String? = null,
)