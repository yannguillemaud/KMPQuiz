package ygmd.kmpquiz.domain.usecase

import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.data.repository.service.FailureType
import ygmd.kmpquiz.data.repository.service.FetchQanda
import ygmd.kmpquiz.data.repository.service.FetchResult
import ygmd.kmpquiz.data.service.QandaFetcher
import ygmd.kmpquiz.data.service.QandaSource

class FetchQandasUseCase (
    private val fetchers: Map<QandaSource, QandaFetcher>,
    private val defaultSource: QandaSource = QandaSource.OPEN_TRIVIA,
): FetchQanda {
    override suspend fun fetch(source: QandaSource): FetchResult<List<Qanda>> {
        val resolvedSource = if(source == QandaSource.DEFAULT) defaultSource else source
        val fetcher = fetchers[resolvedSource]
            ?: return FetchResult.Failure(
                type = FailureType.ERROR,
                message = "No fetcher for source: $resolvedSource",
                cause = IllegalArgumentException()
            )
        return fetcher.fetch()
    }
}