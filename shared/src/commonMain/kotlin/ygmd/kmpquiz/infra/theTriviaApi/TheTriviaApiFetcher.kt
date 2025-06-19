package ygmd.kmpquiz.infra.theTriviaApi

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ygmd.kmpquiz.data.repository.service.FailureType
import ygmd.kmpquiz.data.repository.service.FetchResult
import ygmd.kmpquiz.data.service.FetchConfig
import ygmd.kmpquiz.data.service.QandaFetcher
import ygmd.kmpquiz.domain.entities.qanda.Qanda

private val logger = Logger.withTag("TheTriviaApiFetcher")

class TheTriviaApiFetcher(
    private val client: HttpClient,
) : QandaFetcher {
    override val enabled: Boolean = true

    private val url = "https://the-trivia-api.com/v2/questions/"

    private fun processSuccessResponse(bodyAsText: String): FetchResult<TheTriviaApiResponse> {
        if (bodyAsText.isBlank()) return FetchResult.Failure(
            type = FailureType.API_ERROR,
            message = "Réponse vide du serveur"
        )

        val apiResponse: TheTriviaApiResponse =
            Json.decodeFromString<TheTriviaApiResponse>(bodyAsText)
        return FetchResult.Success(TheTriviaApiResponse(apiResponse.value))
    }

    override suspend fun fetch(fetchConfig: FetchConfig): FetchResult<List<Qanda>> {
        logger.i { "Fetching from $url" }
        try {
            val response: HttpResponse = client.get(url)
            if (response.status.isSuccess().not()) return FetchResult.Failure(
                FailureType.API_ERROR,
                "Api Error $response"
            )

            val qandas = buildList<Qanda> {

            }

        }
    }
}

@Serializable
data class DumbResponse(val type: String)