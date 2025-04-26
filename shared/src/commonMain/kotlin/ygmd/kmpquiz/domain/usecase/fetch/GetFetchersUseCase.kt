package ygmd.kmpquiz.domain.usecase.fetch

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.model.fetcher.QandaFetcher
import ygmd.kmpquiz.domain.repository.FetchRepository

class GetFetchersUseCase(
    private val fetchersRepository: FetchRepository
){
    fun observeFetchers(): Flow<List<QandaFetcher>> = fetchersRepository.observeFetchers()
}