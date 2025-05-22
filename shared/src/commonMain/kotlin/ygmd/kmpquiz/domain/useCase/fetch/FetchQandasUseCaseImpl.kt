package ygmd.kmpquiz.domain.useCase.fetch

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.serialization.json.Json
import ygmd.kmpquiz.domain.pojo.QANDA

class FetchQandasUseCase(private val client: HttpClient) {
    private var cachedResult: Deferred<Result<List<QANDA>>>? = null

    suspend operator fun invoke(): Result<List<QANDA>> {
        cachedResult?.let { return it.await() }

        val deferred = CoroutineScope(Dispatchers.IO)
            .async {
                fetchFromApi()
                    .fold(
                        onFailure = { Result.failure(it) },
                        onSuccess = { Result.success(it.toQandas()) }
                    )
            }
        cachedResult = deferred
        return deferred.await()
    }


    private suspend fun fetchFromApi(): Result<QuizResultDto> {
        println("Fetching")
        try {
            val response = client.get(OpenTriviaDb.DEFAULT_URL)
                .bodyAsText()
            return Json.decodeFromString<QuizResultDto>(response)
                .also { println("Fetched ${it.results.size} Qandas") }
                .let { Result.success(it) }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}

private fun QuizResultDto.toQandas() = results.map { it.toQanda() }