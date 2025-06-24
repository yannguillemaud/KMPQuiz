package ygmd.kmpquiz.application.usecase.qanda

import ygmd.kmpquiz.data.repository.service.FetchResult
import ygmd.kmpquiz.data.service.FetchConfig
import ygmd.kmpquiz.data.service.QandaFetcher
import ygmd.kmpquiz.domain.entities.qanda.Qanda

class FetchQandasUseCase(
    private val fetcher: QandaFetcher,
) {
    suspend operator fun invoke(): FetchResult<List<Qanda>> =
        fetcher.fetch(FetchConfig())
}