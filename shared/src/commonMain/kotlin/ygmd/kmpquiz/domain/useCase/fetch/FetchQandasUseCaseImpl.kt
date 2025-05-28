package ygmd.kmpquiz.domain.useCase.fetch

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import ygmd.kmpquiz.domain.pojo.InternalQanda
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

class OpenTriviaFetchQanda(
    private val client: HttpClient,
) : FetchQandaService {
    // TODO improve with sealed class for responses
    override suspend fun fetch(): Result<List<InternalQanda>> =
        try {
            val response: HttpResponse = client.get(OpenTriviaProperties.DEFAULT_URL)
            if(response.status.isSuccess().not()) throw IOException()
            val fetchedQandas = processResponse(response)
            success(fetchedQandas)
        } catch(serializationException: SerializationException){
            failure(serializationException)
        } catch (iaException: IllegalArgumentException) {
            failure(iaException)
        }

    private suspend fun processResponse(response: HttpResponse): List<InternalQanda> {
        val body = response.bodyAsText()
        val result = Json.decodeFromString<TriviaApiResponse>(body)
        val fetchedQandas = result.results.map { it.toInternal() }
        return fetchedQandas
    }
}