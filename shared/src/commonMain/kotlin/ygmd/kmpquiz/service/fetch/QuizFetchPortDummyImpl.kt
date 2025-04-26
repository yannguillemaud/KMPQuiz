package ygmd.kmpquiz.service.fetch

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.Charsets
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.reflect.TypeInfo
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.charsets.Charsets
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ygmd.kmpquiz.domain.QANDA
import ygmd.kmpquiz.domain.Quiz
import ygmd.kmpquiz.domain.QuizFetchPort

class QuizFetchPortDummyImpl: QuizFetchPort {
    val client = HttpClient {
            install(ContentNegotiation){
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }

        Charsets {
            responseCharsetFallback = Charsets.UTF_8
        }
    }

    override suspend fun fetchQuizzes(): Result<List<Quiz>> {
        val quizzes = coroutineScope {
            val triviaReponse: TriviaResponse = client
                .get("https://opentdb.com/api.php?amount=50")
                .body()

            triviaReponse.results
                ?.groupBy { it.category }
                ?.mapValues {
                    Quiz(category = it.key, qandas = it.value.map {
                        QANDA(
                            question = it.question,
                            answers = it.incorrect_answers,
                            correctAnswer = it.correct_answer
                        )
                    })
                }?.values?.toList()
        }
        return quizzes?.let { Result.success(it) } ?: Result.failure(Throwable("Could not fetch quizzes"))
    }
}

@Serializable
data class TriviaResponse(val response_code: Int, val results: List<TriviaQuiz>?)

@Serializable
data class TriviaQuiz(
    val type: String,
    val difficulty: String,
    val category: String,
    val question: String,
    val correct_answer: String,
    val incorrect_answers: List<String>
)