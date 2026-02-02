package ygmd.kmpquiz.infra.cs2

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ygmd.kmpquiz.domain.git.GitTreeNode
import ygmd.kmpquiz.domain.git.GitTreeResponse
import ygmd.kmpquiz.domain.model.draftqanda.DraftQanda
import ygmd.kmpquiz.domain.model.qanda.AnswersFactory
import ygmd.kmpquiz.domain.model.qanda.Question
import ygmd.kmpquiz.domain.result.FailureType.NETWORK_ERROR
import ygmd.kmpquiz.domain.result.FailureType.RATE_LIMIT
import ygmd.kmpquiz.domain.result.FailureType.SERVER_ERROR
import ygmd.kmpquiz.domain.result.FailureType.UNKNOWN_ERROR
import ygmd.kmpquiz.domain.result.FetchResult
import ygmd.kmpquiz.domain.service.Fetcher
import java.util.UUID

private const val GITHUB_API_BASE_URL = "https://api.github.com"
private const val GITHUB_REPO_OWNER = "yannguillemaud"
private const val GITHUB_REPO_NAME = "cs2-map-positions"
private const val GITHUB_BRANCH = "main"
private const val GITHUB_RAW_BASE_URL = "https://raw.githubusercontent.com"
private val logger = Logger.withTag("LocalImageFetcher")

private const val TREE_API_URL = "git/trees/$GITHUB_BRANCH?recursive=1"

data class PositionInfos(
    val name: String,
    val url: String,
)

class CS2MapPositionsFetcher(
    private val httpClient: HttpClient,
) : Fetcher {
    private val uuid = UUID.randomUUID().toString()
    override val id: String = uuid

    override val name: String
        get() = "CS2 Callouts"

    private val cs2ApiUrl: String =
        "$GITHUB_API_BASE_URL/repos/$GITHUB_REPO_OWNER/$GITHUB_REPO_NAME/$TREE_API_URL"

    override suspend fun fetch(): FetchResult<List<DraftQanda>> {
        logger.i { "Fetching images from: $cs2ApiUrl" }
        return try {
            val gitTreeResponse: GitTreeResponse = withContext(Dispatchers.IO) {
                httpClient.get(cs2ApiUrl).body<GitTreeResponse>()
            }
            if (gitTreeResponse.truncated) {
                logger.w { "Git Tree Api Response is trunated. Implementation for recursive fetch is not implemented yet" }
            }
            val qandas = handleTreeNodes(gitTreeResponse.tree)
            FetchResult.Success(qandas)
        } catch (e: RedirectResponseException) { // 3xx errors
            logger.e(e) { "GitHub API request was redirected. URL: $TREE_API_URL" }
            FetchResult.Failure(
                type = NETWORK_ERROR,
                message = "Network error (redirection): ${e.response.status.description}"
            )
        } catch (e: ClientRequestException) { // 4xx errors
            logger.e(e) { "Client error during GitHub API request. URL: $TREE_API_URL, Status: ${e.response.status}" }
            handleClientRequestException(e)
        } catch (e: ServerResponseException) { // 5xx errors
            logger.e(e) { "Server error during GitHub API request. URL: $TREE_API_URL" }
            FetchResult.Failure(
                type = SERVER_ERROR, // Nouveau type d'erreur possible
                message = "Github server error: ${e.response.status.description}"
            )
        } catch (e: Exception) { // serializationException, unknownHostException...
            logger.e(e) { "Generic error while fetching images from GitHub. URL: $TREE_API_URL" }
            FetchResult.Failure(
                type = UNKNOWN_ERROR,
                message = "Unknown error: ${e.message ?: "Unknown error occured"}"
            )
        }
    }

    private fun handleClientRequestException(e: ClientRequestException): FetchResult.Failure =
        if (e.response.status == HttpStatusCode.Forbidden) {
            logger.w { "GitHub API rate limit likely exceeded or token is invalid/missing." }
            FetchResult.Failure(
                type = RATE_LIMIT,
                message = "Rate limit exceeded"
            )
        } else {
            FetchResult.Failure(
                type = NETWORK_ERROR,
                message = "Client error: ${e.response.status.description}"
            )
        }

    private fun handleTreeNodes(nodes: List<GitTreeNode>): List<DraftQanda> {
        return nodes
            .toMap()
            .flatMap { (mapName, groupNodes) ->
                if (groupNodes.size < 2) {
                    logger.w { "Not enough nodes for map $mapName. Skipping." }
                    return@flatMap emptyList()
                }

                val shuffledNodes = groupNodes.shuffled()
                val size = shuffledNodes.size
                val actualWrongCount = minOf(3, size - 1)

                shuffledNodes.mapIndexed { index, currentNode ->
                    val incorrectPaths = (1..actualWrongCount).map { offset ->
                        shuffledNodes[(index + offset) % size].path
                    }

                    DraftQanda(
                        question = Question.ImageQuestion(
                            imageUrl = buildGithubRawUrl(currentNode.path),
                            text = "How is this callout called ?"
                        ),
                        answers = AnswersFactory.createMultipleTextChoices(
                            correctAnswer = currentNode.path.substringAfter("/")
                                .substringBeforeLast("."),
                            incorrectAnswers = incorrectPaths.map {
                                it.substringAfter("/").substringBeforeLast(".")
                            }
                        ),
                        categoryName = mapName
                    )
                }
            }
    }


    private fun buildGithubRawUrl(filePath: String): String {
        return "$GITHUB_RAW_BASE_URL/$GITHUB_REPO_OWNER/$GITHUB_REPO_NAME/$GITHUB_BRANCH/$filePath"
    }
}

private fun List<GitTreeNode>.toMap(): Map<String, List<GitTreeNode>> =
    filter { it.type == "blob" && it.path.isImageFile() }
        .groupBy { it.path.substringBefore("/") }

private fun String.isImageFile(): Boolean = endsWith(".png")

