package ygmd.kmpquiz.domain.useCase.fetch

import io.ktor.client.HttpClient
import io.ktor.client.plugins.Charsets
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ygmd.kmpquiz.domain.pojo.QANDA

class FetchQandasUseCase(private val client: HttpClient) {
    private var _quizResultDto: QuizResultDto? = null

    suspend operator fun invoke(): List<QANDA> {
        _quizResultDto = _quizResultDto ?: fetchFromApi()
        return _quizResultDto?.results?.map { it.toQanda() }.orEmpty()
    }

    private suspend fun fetchFromApi(): QuizResultDto {
        println("Fetching")
        val response = client.get(OpenTriviaDb.DEFAULT_URL)
            .bodyAsText()
        return Json.decodeFromString<QuizResultDto>(response)
            .also {
                println("Fetched ${it.results.size} Qandas")
            }
    }
}