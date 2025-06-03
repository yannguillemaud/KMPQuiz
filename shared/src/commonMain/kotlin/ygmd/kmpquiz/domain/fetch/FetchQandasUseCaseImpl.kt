package ygmd.kmpquiz.domain.fetch

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.TooManyRequests
import io.ktor.http.isSuccess
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import ygmd.kmpquiz.domain.fetch.opentrivia.OpenTriviaProperties
import ygmd.kmpquiz.domain.fetch.opentrivia.TriviaApiResponse
import ygmd.kmpquiz.domain.pojo.InternalQanda
import java.time.Duration

class ApiException(message: String) : Exception(message)
class ParseException(message: String, cause: Throwable) : Exception(message, cause)

class OpenTriviaFetchQanda(
    private val client: HttpClient,
    private val logger: Logger,
) : FetchQandaService {
    override suspend fun fetch(): FetchResult<List<InternalQanda>> {
        return try {
            val response = client.get(OpenTriviaProperties.DEFAULT_URL)

            when {
                response.status.isSuccess() -> {
                    val data = processResponse(response)
                    logger.i { "Data fetched: $data" }
                    FetchResult.Success(data)
                }

                response.status == TooManyRequests -> {
                    val retryAfter: Duration? = response.headers["Retry-After"]
                        ?.toLongOrNull()
                        ?.let { Duration.ofSeconds(it) }

                    logger.w { "Rate limited, retry after: $retryAfter" }
                    FetchResult.RateLimit(retryAfter)
                }

                response.status.value in 400..499 -> {
                    val errorMsg = "Client error: ${response.status}"
                    logger.e { errorMsg }
                    FetchResult.ApiError(response.status.value, errorMsg)
                }

                response.status.value in 500..599 -> {
                    val errorMsg = "Server error: ${response.status}"
                    logger.e { errorMsg }
                    FetchResult.ApiError(response.status.value, errorMsg)
                }

                else -> {
                    val errorMsg = "Unexpected status: ${response.status}"
                    logger.e { errorMsg }
                    FetchResult.ApiError(response.status.value, errorMsg)
                }
            }
        } catch (e: Exception) {
            logger.e(e) { "Network exception occurred: ${e.message}" }
            FetchResult.Error(e)
        }
    }

    private suspend fun processResponse(response: HttpResponse): List<InternalQanda> =
        try {
            val body = response.bodyAsText()
            if (body.isBlank()) throw IllegalStateException("Empty response body")

            val result = Json.decodeFromString<TriviaApiResponse>(body)
            when (result.response_code) {
                0 -> result.results.map { it.toInternal() }
                1 -> throw ApiException("No results returned")
                2 -> throw ApiException("Invalid parameter")
                3 -> throw ApiException("Token not found")
                4 -> throw ApiException("Token empty")
                else -> throw ApiException("Unknown API error: ${result.response_code}")
            }
        } catch (e: SerializationException) {
            logger.e(e) { "Failed to parse response: ${e.message}" }
            throw ParseException("Invalid JSON response", e)
        }
}