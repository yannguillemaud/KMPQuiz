package ygmd.kmpquiz

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.headers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import ygmd.kmpquiz.domain.useCase.fetch.FetchedQanda
import ygmd.kmpquiz.domain.useCase.fetch.OpenTriviaFetchQanda
import kotlin.test.Test

class OpenTriviaFetchQandaTest {
    private fun mockEngine(status: HttpStatusCode = OK, response: String) = MockEngine { request ->
        respond(
            status = status,
            content = response,
            headers = headers { ContentType to "application/json" }
        )
    }

    @Test
    fun `should return error when fetch fail when too much retry`() = runTest {
        // GIVEN
        val mockEngine = mockEngine(response = FAKED_ERROR_RESPONSE_JSON)
        val client = HttpClient(mockEngine)
        val fetchUseCase = OpenTriviaFetchQanda(client)

        // WHEN
        val result = fetchUseCase.fetch()
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `should return qandas when fetch success`() = runTest {
        // GIVEN
        // GIVEN
        val mockEngine = mockEngine(response = FAKED_SUCCESS_RESPONSE_JSON)
        val client = HttpClient(mockEngine)
        val fetchUseCase = OpenTriviaFetchQanda(client)

        // WHEN
        val result = fetchUseCase.fetch()
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrThrow())
            .hasSize(1)
            .usingRecursiveComparison()
            .isEqualTo(listOf(
                FetchedQanda(
                    difficulty = "hard",
                    category = "Politics",
                    question = "Which US state was the first to allow women to vote in 1869?",
                    correctAnswerPosition = 3,
                    answers = listOf(
                        "California","Delaware","Virginia","Wyoming"
                    )
                )
            ))
    }
}

val FAKED_SUCCESS_RESPONSE_JSON =
    """
        { 
          "response_code":0,
          "results":[
              { 
                "type":"multiple",
                "difficulty":"hard",
                "category":"Politics",
                "question":"Which US state was the first to allow women to vote in 1869?",
                "correct_answer":"Wyoming",
                "incorrect_answers":["California","Delaware","Virginia"]
              }
          ]
        }
    """.trimIndent()

val FAKED_ERROR_RESPONSE_JSON =
    """
        {
            "response_code": 5,
            "result": []
        }
    """.trimIndent()