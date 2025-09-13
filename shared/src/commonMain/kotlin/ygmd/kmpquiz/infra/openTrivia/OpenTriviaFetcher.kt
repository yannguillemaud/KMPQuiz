package ygmd.kmpquiz.infra.openTrivia

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.TooManyRequests
import io.ktor.http.isSuccess
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import ygmd.kmpquiz.data.service.FailureType
import ygmd.kmpquiz.data.service.FetchConfig
import ygmd.kmpquiz.data.service.FetchResult
import ygmd.kmpquiz.data.service.QandaFetcher
import ygmd.kmpquiz.domain.entities.qanda.AnswersFactory
import ygmd.kmpquiz.domain.entities.qanda.Question
import ygmd.kmpquiz.domain.repository.DraftQanda
import ygmd.kmpquiz.infra.unescaped
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private val logger = Logger.withTag("OpenTriviaFetcher")

class OpenTriviaFetcher(
    private val client: HttpClient,
) : QandaFetcher {
    override val isEnabled: Boolean = true

    private val url = OpenTriviaUrlBuilder().withAmount(50)

    override suspend fun fetch(
        fetchConfig: FetchConfig
    ): FetchResult<List<DraftQanda>> =
        try {
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

private suspend fun handleHttpResponse(response: HttpResponse): FetchResult<List<DraftQanda>> {
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

private suspend fun processSuccessResponse(response: HttpResponse): FetchResult<List<DraftQanda>> {
    return try {
        val body = response.bodyAsText()
        if (body.isBlank()) {
            return FetchResult.Failure(
                type = FailureType.API_ERROR,
                message = "Réponse vide du serveur"
            )
        }

        val apiResponse = Json.decodeFromString<OpenTriviaApiResponse>(body)
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

private fun handleApiResponse(apiResponse: OpenTriviaApiResponse): FetchResult<List<DraftQanda>> {
    return when (apiResponse.response_code) {
        0 -> {
            val qandas = apiResponse.results.map { it.toFetchedQanda() }
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

fun OpenTriviaQandaDto.toFetchedQanda(): DraftQanda {
    val unescapedQuestion = question.unescaped()
    val unescapedCorrectAnswer = correct_answer
    val unescapedIncorrectAnswers = incorrect_answers
    val unescapedCategory = category.sanitized()
    val isBooleanType = incorrect_answers.size == 2

    return DraftQanda(
        question = Question.TextQuestion(unescapedQuestion),
        answers = if (isBooleanType) AnswersFactory.createTrueFalse(correct_answer.toBooleanStrict())
        else AnswersFactory.createMultipleTextChoices(
            unescapedCorrectAnswer,
            unescapedIncorrectAnswers
        ),
        category = unescapedCategory
    )
}

private fun String.sanitized(): String = this.replace("Entertainment: ", "")
