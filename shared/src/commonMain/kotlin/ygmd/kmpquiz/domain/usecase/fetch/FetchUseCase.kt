package ygmd.kmpquiz.domain.usecase.fetch

import ygmd.kmpquiz.domain.model.draftqanda.DraftQanda
import ygmd.kmpquiz.domain.repository.FetchRepository
import ygmd.kmpquiz.domain.result.FailureType
import ygmd.kmpquiz.domain.result.FetchResult

class FetchUseCase(
    private val fetchRepository: FetchRepository,
) {
    suspend fun execute(fetcherId: String): FetchResult<List<DraftQanda>> {
        val fetcher = fetchRepository.getFetcherById(fetcherId).getOrNull() ?: return FetchResult.Failure(
            FailureType.ERROR, "Fetcher not found for id: $fetcherId"
        )
        fetchRepository.updateFetcher(
            fetcherId = fetcherId,
            qandaFetcher = fetcher.copy(isFetching = true)
        )
        val result = fetchRepository.fetch(fetcher)
        fetchRepository.updateFetcher(
            fetcherId = fetcherId,
            qandaFetcher = fetcher.copy(isFetching = false)
        )
        return result
    }
}