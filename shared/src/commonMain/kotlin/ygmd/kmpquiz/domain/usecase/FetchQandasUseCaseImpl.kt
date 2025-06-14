package ygmd.kmpquiz.domain.usecase

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.TooManyRequests
import io.ktor.http.isSuccess
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import opentrivia.OpenTriviaUrlBuilder
import opentrivia.TriviaApiResponse
import ygmd.kmpquiz.domain.pojo.qanda.InternalQanda
import ygmd.kmpquiz.domain.usecase.FetchQandaUseCase.FetchResult
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class OpenTriviaFetchQanda(
    private val client: HttpClient,
    private val logger: Logger,
) : FetchQandaUseCase {
    private val url = OpenTriviaUrlBuilder()
        .withAmount(50)

    override suspend fun fetch(): FetchResult<List<InternalQanda>> {
        return try {
            val response = client.get(url.build())
            handleHttpResponse(response)
        } catch (e: Exception) {
            logger.e(e) { "Network exception: ${e.message}" }
            FetchResult.Failure(
                type = FailureType.NETWORK_ERROR,
                message = "Erreur réseau: ${e.message}",
                cause = e
            )
        }
    }

    private suspend fun handleHttpResponse(response: HttpResponse): FetchResult<List<InternalQanda>> {
        return when {
            response.status.isSuccess() -> {
                processSuccessResponse(response)
            }

            response.status == TooManyRequests -> {
                val retryAfter = parseRetryAfter(response)
                logger.w { "Rate limited, retry after: $retryAfter" }
                FetchResult.Failure(
                    type = FailureType.RATE_LIMIT,
                    message = "Trop de requêtes, réessayez plus tard",
                    retryAfter = retryAfter
                )
            }

            else -> {
                val message = "Erreur HTTP ${response.status.value}: ${response.status.description}"
                logger.e { message }
                FetchResult.Failure(
                    type = FailureType.API_ERROR,
                    message = message
                )
            }
        }
    }

    private suspend fun processSuccessResponse(response: HttpResponse): FetchResult<List<InternalQanda>> {
        return try {
            val body = response.bodyAsText()
            if (body.isBlank()) {
                return FetchResult.Failure(
                    type = FailureType.API_ERROR,
                    message = "Réponse vide du serveur"
                )
            }

            val apiResponse = Json.decodeFromString<TriviaApiResponse>(body)
            handleApiResponse(apiResponse)

        } catch (e: SerializationException) {
            logger.e(e) { "Failed to parse response: ${e.message}" }
            FetchResult.Failure(
                type = FailureType.NETWORK_ERROR,
                message = "Erreur de parsing: réponse invalide",
                cause = e
            )
        }
    }

    private fun handleApiResponse(apiResponse: TriviaApiResponse): FetchResult<List<InternalQanda>> {
        return when (apiResponse.response_code) {
            0 -> {
                val qandas = apiResponse.results.map { it.toInternal() }
                logger.i { "${qandas.size} qandas fetched successfully" }
                FetchResult.Success(qandas)
            }

            1 -> {
                logger.i { "No results returned from API" }
                FetchResult.Success(emptyList())
            }

            2 -> FetchResult.Failure(
                type = FailureType.API_ERROR,
                message = "Paramètres invalides"
            )

            3 -> FetchResult.Failure(
                type = FailureType.API_ERROR,
                message = "Token non trouvé"
            )

            4 -> FetchResult.Failure(
                type = FailureType.API_ERROR,
                message = "Token vide"
            )

            else -> FetchResult.Failure(
                type = FailureType.API_ERROR,
                message = "Erreur API inconnue: ${apiResponse.response_code}"
            )
        }
    }

    private fun parseRetryAfter(response: HttpResponse): Duration? {
        return response.headers["Retry-After"]
            ?.toLongOrNull()
            ?.takeIf { it > 0 }
            ?.seconds
    }
}