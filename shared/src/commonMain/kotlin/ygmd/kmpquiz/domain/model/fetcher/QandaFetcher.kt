package ygmd.kmpquiz.domain.model.fetcher

import ygmd.kmpquiz.domain.service.Fetcher

data class QandaFetcher(
    val id: String,
    val name: String,
    val isEnabled: Boolean,
    val fetcher: Fetcher,
    val isFetching: Boolean = false,
)
