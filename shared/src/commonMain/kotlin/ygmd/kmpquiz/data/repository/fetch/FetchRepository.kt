package ygmd.kmpquiz.data.repository.fetch

import ygmd.kmpquiz.domain.model.fetcher.QandaFetcher
import ygmd.kmpquiz.domain.service.Fetcher

class FetchRepository(private val fetchers: List<Fetcher>) {
    fun getAll(): List<QandaFetcher> = fetchers.map {
        QandaFetcher(
            id = it.id,
            name = it.name,
            fetcher = it
        )
    }
}