package ygmd.kmpquiz.application.usecase.fetch

import ygmd.kmpquiz.data.service.FetchConfig
import ygmd.kmpquiz.data.service.FetchResult
import ygmd.kmpquiz.data.service.QandaFetcher
import ygmd.kmpquiz.domain.repository.DraftQanda
import ygmd.kmpquiz.domain.repository.FetchRepository

class FetchQandasUseCase(
    private val fetcher: QandaFetcher,
    private val fetchRepository: FetchRepository,
) {
    suspend fun fetch(): Result<List<DraftQanda>> {
        return when(val result = fetcher.fetch(FetchConfig())){
            is FetchResult.Failure -> Result.failure(result.cause ?: IllegalStateException())
            is FetchResult.Success<List<DraftQanda>> -> {
                fetchRepository.saveDrafted(result.data)
                Result.success(result.data)
            }
        }
    }
}