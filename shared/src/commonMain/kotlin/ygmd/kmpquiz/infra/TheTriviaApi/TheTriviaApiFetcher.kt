package ygmd.kmpquiz.infra.TheTriviaApi

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import ygmd.kmpquiz.data.repository.service.FetchResult
import ygmd.kmpquiz.data.service.FetchConfig
import ygmd.kmpquiz.data.service.QandaFetcher
import ygmd.kmpquiz.domain.entities.qanda.Qanda

class TheTriviaApiFetcher(
    private val client: HttpClient,
    private val logger: Logger,
): QandaFetcher {
    override val enabled: Boolean = true

    private val url = "https://the-trivia-api.com/v2/questions/"

    private fun parseResult(httpResponse: HttpResponse): List<Any> {
        return emptyList()
    }

    override suspend fun fetch(fetchConfig: FetchConfig): FetchResult<List<Qanda>> {
        logger.i { "Fetching from $url" }
        try {
            val result = client.get(url)
            val dtos = parseResult(result)
            val qandas = dtos.toQandas()
            return FetchResult.Success(qandas)
        }
    }
}

sealed class TheTriviaApiChoice {
    data class
}