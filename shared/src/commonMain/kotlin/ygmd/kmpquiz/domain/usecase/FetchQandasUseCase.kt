package ygmd.kmpquiz.domain.usecase

import ygmd.kmpquiz.domain.pojo.qanda.QandaContent
import ygmd.kmpquiz.domain.service.FailureType
import ygmd.kmpquiz.domain.service.FetchQanda
import ygmd.kmpquiz.domain.service.FetchResult
import ygmd.kmpquiz.domain.service.QandaFetcher
import ygmd.kmpquiz.domain.service.QandaSource

class FetchQandasUseCase (
    private val fetchers: Map<QandaSource, QandaFetcher>,
    private val defaultSource: QandaSource = QandaSource.OPEN_TRIVIA,
): FetchQanda {
    override suspend fun fetch(source: QandaSource): FetchResult<List<QandaContent>> {
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