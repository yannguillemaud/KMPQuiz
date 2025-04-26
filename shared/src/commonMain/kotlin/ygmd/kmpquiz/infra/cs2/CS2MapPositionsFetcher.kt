package ygmd.kmpquiz.infra.cs2

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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

const val GITHUB_API_BASE_URL = "https://api.github.com"
const val GITHUB_REPO_OWNER = "yannguillemaud"
const val GITHUB_REPO_NAME = "cs2-map-positions"
const val GITHUB_BRANCH = "main"
const val GITHUB_RAW_BASE_URL = "https://raw.githubusercontent.com"
private val logger = Logger.withTag("LocalImageFetcher")

/**
 * TODO - refacto is needed to avoid github's rate limit
 * maybe use last commit date or hash
 * TODO - implement pagination if response.truncated is true
 */
class CS2MapPositionsFetcher(
    private val httpClient: HttpClient,
) : Fetcher {
    override val name: String
        get() = "CS2 Map Position Fetcher"
    private val treeApiUrl = buildGithubApiUrl("/git/trees/$GITHUB_BRANCH?recursive=1")
    private val uuid = UUID.randomUUID().toString()

    override val id: String
        get() = uuid

    override suspend fun fetch(): FetchResult<List<DraftQanda>> {
        return try {
            logger.i { "Fetching images from: $treeApiUrl" }
            val gitTreeResponse = httpClient.get(treeApiUrl) {
                header("Accept", "application/vnd.github.v3+json")
                System.getenv("GITHUB_TOKEN")?.let { token -> "token $token" }
            }.body<GitTreeResponse>()

            if (gitTreeResponse.truncated) {
                logger.w { "Git Tree Api Response is trunated. Implementation for recursive fetch is not implemented yet" }
            }

            val qandas = coroutineScope {
                handleTreeNodes(gitTreeResponse.tree, this)
            }
            FetchResult.Success(qandas)
        } catch (e: RedirectResponseException) { // 3xx errors
            logger.e(e) { "GitHub API request was redirected. URL: $treeApiUrl" }
            FetchResult.Failure(
                type = NETWORK_ERROR,
                message = "Network error (redirection): ${e.response.status.description}"
            )
        } catch (e: ClientRequestException) { // 4xx errors
            logger.e(e) { "Client error during GitHub API request. URL: $treeApiUrl, Status: ${e.response.status}" }
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
        } catch (e: ServerResponseException) { // 5xx errors
            logger.e(e) { "Server error during GitHub API request. URL: $treeApiUrl" }
            FetchResult.Failure(
                type = SERVER_ERROR, // Nouveau type d'erreur possible
                message = "Github server error: ${e.response.status.description}"
            )
        } catch (e: Exception) { // serializationException, unknownHostException...
            logger.e(e) { "Generic error while fetching images from GitHub. URL: $treeApiUrl" }
            FetchResult.Failure(
                type = UNKNOWN_ERROR,
                message = "Unknown error: ${e.message ?: "Unknown error occured"}"
            )
        }
    }

    private suspend fun handleTreeNodes(
        nodes: List<GitTreeNode>,
        scope: CoroutineScope
    ): List<DraftQanda> {
        val nodesByMap = nodes.groupBy { it.path.substringBefore('/') }

        val deferredQandas = nodesByMap.mapNotNull { (mapName, mapNodes) ->
            scope.async {
                try {
                    val answersByFile = mapNodes
                        .filter { it.path.isImageFile() }
                        .associate {
                            it.path.substringAfterLast('/')
                                .substringBeforeLast('.') to buildGithubRawUrl(it.path)
                        }
                    answersByFile.map {
                        DraftQanda(
                            question = Question.ImageQuestion(imageUrl = it.value),
                            answers = AnswersFactory.createMultipleTextChoices(
                                correctAnswer = it.key,
                                incorrectAnswers = answersByFile.keys.filter { key -> key != it.key }.shuffled()
                                    .take(3)
                            ),
                            categoryName = "CS2 - $mapName"
                        )
                    }
                } catch (e: Exception) {
                    logger.e(e) { "Failed to process map node: $mapName" }
                    null
                }
            }
        }
        return deferredQandas.awaitAll().filterNotNull().flatten()
    }

    private fun buildGithubApiUrl(path: String): String {
        return "$GITHUB_API_BASE_URL/repos/$GITHUB_REPO_OWNER/$GITHUB_REPO_NAME$path"
    }

    private fun buildGithubRawUrl(filePath: String): String {
        return "$GITHUB_RAW_BASE_URL/$GITHUB_REPO_OWNER/$GITHUB_REPO_NAME/$GITHUB_BRANCH/$filePath"
    }
}

private val String.extensionFile get() = substringAfterLast(".")

private fun String.isImageFile(): Boolean =
    extensionFile in listOf("jpg", "jpeg", "png")

