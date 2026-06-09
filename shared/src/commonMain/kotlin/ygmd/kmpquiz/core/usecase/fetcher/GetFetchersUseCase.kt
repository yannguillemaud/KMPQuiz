package ygmd.kmpquiz.core.usecase.fetcher

import ygmd.kmpquiz.core.repository.FetcherRepository

class GetFetchersUseCase(private val fetcherRepository: FetcherRepository) {
    fun observeFetchers() = fetcherRepository.observeFetchers()
}