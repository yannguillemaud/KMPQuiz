package ygmd.kmpquiz.core.domain.fetcher

import ygmd.kmpquiz.core.service.fetcher.Fetcher

data class QandaFetcher(
    val id: String,
    val name: String,
    val fetcher: Fetcher,
    val state: FetcherState,
)

sealed interface FetcherState {
    data object Idle : FetcherState
    data object Fetching : FetcherState
    data object Success : FetcherState
    data class Error(val message: String) : FetcherState
}
