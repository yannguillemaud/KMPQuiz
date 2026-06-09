package ygmd.kmpquiz.infra.fetcher.cs2

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode.Companion.Forbidden
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ygmd.kmpquiz.core.domain.qanda.QandaDetails
import ygmd.kmpquiz.core.domain.qanda.QuestionContent
import ygmd.kmpquiz.core.domain.errors.FetchError.NetworkError
import ygmd.kmpquiz.core.domain.errors.FetchError.RateLimitError
import ygmd.kmpquiz.core.domain.errors.FetchError.ServerError
import ygmd.kmpquiz.core.domain.errors.FetchError.UnknownError
import ygmd.kmpquiz.core.domain.qanda.AnswerContent
import ygmd.kmpquiz.core.domain.qanda.Answers
import ygmd.kmpquiz.core.service.fetcher.Fetcher
import ygmd.kmpquiz.infra.fetcher.cs2.CS2QandaFetcherConstants.API_URL
import ygmd.kmpquiz.infra.git.GitTreeNode
import ygmd.kmpquiz.infra.git.GitTreeResponse
import java.util.UUID

data class PositionDetails(
    val mapName: String,
    val positionName: String,
    val positionImageUrl: String,
)

private val logger = Logger.withTag("CS2QandaFetcher")

class CS2Fetcher(
    private val httpClient: HttpClient,
    private val dispatcher: CoroutineDispatcher,
) : Fetcher {
    override val name: String
        get() = "CS2 Callouts"

    override suspend operator fun invoke(): Result<List<QandaDetails>> {
        return try {
            val gitTreeResponse = withContext(dispatcher) {
                httpClient
                    .get(API_URL)
                    .body<GitTreeResponse>()
            }
            val positionsDetails = handleTreeNodes(gitTreeResponse.tree)
            val qandaDetails = createQandaDetails(positionsDetails = positionsDetails)
            Result.success(qandaDetails)
        } catch (e: Exception) {
            logger.e(e) { "Fetch failed: $e" }
            when (e) {
                is ClientRequestException -> Result.failure(NetworkError())
                is ServerResponseException -> Result.failure(ServerError())
                is RedirectResponseException -> {
                    val exception =
                        if (e.response.status == Forbidden) RateLimitError() else NetworkError()
                    Result.failure(exception)
                }

                else -> Result.failure(UnknownError())
            }
        }
    }

    private fun handleTreeNodes(nodes: List<GitTreeNode>): List<PositionDetails> {
        val allPositions = nodes.filter { it.type == "blob" && it.path.isImageFile() }
        return allPositions.map { it.asPositionDetails() }
    }

    private fun createQandaDetails(positionsDetails: List<PositionDetails>): List<QandaDetails> {
        val positionsByMap = positionsDetails.groupBy { it.mapName }
        val qandaDetails = positionsByMap.flatMap { (mapName, positions) ->
            val qandas = mutableListOf<QandaDetails>()
            positions.forEach { positionDetails ->
                val shuffledPositions = positions.shuffled() - positionDetails
                val correctAnswer = positionDetails.positionName
                val incorrectAnswers = shuffledPositions.take(3)
                val correctAnswerContent = AnswerContent.TextAnswerContent(
                    id = UUID.randomUUID().toString(),
                    text = correctAnswer
                )
                qandas.add(
                    QandaDetails(
                        categoryName = mapName,
                        question = QuestionContent.ImageContent(positionDetails.positionImageUrl),
                        answers = Answers(
                            answerContents = incorrectAnswers.map {
                                AnswerContent.TextAnswerContent(
                                    id = UUID.randomUUID().toString(),
                                    text = it.positionName
                                )
                            } + correctAnswerContent,
                            correctAnswer = correctAnswerContent
                        )
                    )
                )
            }
            qandas
        }
        return qandaDetails
    }
}

private fun String.isImageFile(): Boolean = extension() == "png"

private fun GitTreeNode.getMapName(): String = path
    .substringBefore("/")
    .replaceFirstChar { it.uppercase() }

private fun GitTreeNode.getPositionName(): String {
    require(path.isImageFile())
    return path
        .substringAfter("/")
        .substringBeforeLast(".")
}

private fun String.extension() = substringAfterLast(".")

private fun GitTreeNode.asPositionDetails(): PositionDetails {
    val mapName = getMapName()
    val positionName = getPositionName()
    return PositionDetails(
        mapName = mapName,
        positionName = positionName,
        positionImageUrl = CS2QandaFetcherConstants.RAWCONTENT_API_URL + "/$path"
    )
}

