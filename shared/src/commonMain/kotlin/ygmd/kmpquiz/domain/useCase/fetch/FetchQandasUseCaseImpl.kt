package ygmd.kmpquiz.domain.useCase.fetch

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import ygmd.kmpquiz.domain.pojo.InternalQanda

class OpenTriviaFetchQanda(
    private val client: HttpClient
): FetchQandas {
    private val json = Json { ignoreUnknownKeys = true }
    private var cachedResult: Result<List<InternalQanda>>? = null

    override suspend fun fetch(): Result<List<InternalQanda>> =
        cachedResult ?: kotlin.runCatching {
            val response: String = fetchBrut()
            val qandas: List<InternalQanda> = decode(response)
            val success = Result.success(qandas)
            cachedResult = success
            return success
        }


    private suspend fun fetchBrut(): String =
        try {
            client.get(OpenTriviaDb.DEFAULT_URL)
                .bodyAsText()
        } catch (e: Exception){
            throw RuntimeException(e)
        }

    private fun decode(brut: String): List<InternalQanda> {
        return try {
            val dto = json.decodeFromString<QuizResultDto>(brut)
            dto.toQandas()
        } catch (error: SerializationException){
            println("Error when fetching: $error")
            error("Fetching failed: $brut")
        }
    }
}

private fun QuizResultDto.toQandas(): List<InternalQanda> = results.map { it.toQanda() }