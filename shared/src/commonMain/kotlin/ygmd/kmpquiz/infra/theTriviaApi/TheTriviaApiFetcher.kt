package ygmd.kmpquiz.infra.theTriviaApi

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import ygmd.kmpquiz.data.service.FailureType
import ygmd.kmpquiz.data.service.FetchConfig
import ygmd.kmpquiz.data.service.FetchResult
import ygmd.kmpquiz.data.service.FetchResult.Failure
import ygmd.kmpquiz.data.service.QandaFetcher
import ygmd.kmpquiz.domain.repository.DraftQanda

private val logger = Logger.withTag("TheTriviaApiFetcher")

class TheTriviaApiFetcher(
    private val client: HttpClient,
) : QandaFetcher {
    override val isEnabled: Boolean = true

    private val url = "https://the-trivia-api.com/v2/questions/"

    private fun processSuccessResponse(bodyAsText: String): FetchResult<List<DraftQanda>> {
        if (bodyAsText.isBlank()) return Failure(
            type = FailureType.API_ERROR,
            message = "Réponse vide du serveur"
        )

        val apiResponse = Json.decodeFromString<List<TheTriviaApiResponse>>(bodyAsText)
//        val dtos = apiResponse.map { TheTriviaApiMapper.mapToDomain(it) }
        return FetchResult.Success(emptyList())
    }

    override suspend fun fetch(fetchConfig: FetchConfig): FetchResult<List<DraftQanda>> {
        logger.i { "Fetching from $url" }
        val response: HttpResponse = client.get(url)
        if (response.status.isSuccess().not())
            return Failure(FailureType.API_ERROR, "Api Error $response")
        return processSuccessResponse(response.bodyAsText())
    }
}