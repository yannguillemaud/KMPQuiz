package ygmd.kmpquiz.application.usecase.fetch

import ygmd.kmpquiz.data.service.FetchConfig
import ygmd.kmpquiz.data.service.FetchResult
import ygmd.kmpquiz.data.service.QandaFetcher
import ygmd.kmpquiz.domain.repository.DraftQanda

class FetchQandasUseCase(
    private val fetcher: QandaFetcher,
) {
    suspend fun fetch(): Result<List<DraftQanda>> {
        return when(val result = fetcher.fetch(FetchConfig())){
            is FetchResult.Failure -> Result.failure(result.cause ?: IllegalStateException())
            is FetchResult.Success<List<DraftQanda>> -> Result.success(result.data)
        }
    }
}