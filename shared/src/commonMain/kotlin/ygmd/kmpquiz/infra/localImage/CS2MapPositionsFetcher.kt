package ygmd.kmpquiz.infra.localImage

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
import kotlinx.serialization.Serializable
import ygmd.kmpquiz.data.service.FailureType.NETWORK_ERROR
import ygmd.kmpquiz.data.service.FailureType.RATE_LIMIT
import ygmd.kmpquiz.data.service.FailureType.SERVER_ERROR
import ygmd.kmpquiz.data.service.FailureType.UNKNOWN_ERROR
import ygmd.kmpquiz.data.service.FetchConfig
import ygmd.kmpquiz.data.service.FetchResult
import ygmd.kmpquiz.data.service.QandaFetcher
import ygmd.kmpquiz.domain.entities.qanda.AnswersFactory
import ygmd.kmpquiz.domain.entities.qanda.Question
import ygmd.kmpquiz.domain.repository.DraftQanda

const val GITHUB_API_BASE_URL = "https://api.github.com"
const val GITHUB_REPO_OWNER = "yannguillemaud"
const val GITHUB_REPO_NAME = "cs2-map-positions"
const val GITHUB_BRANCH = "test/nuke"
const val GITHUB_RAW_BASE_URL = "https://raw.githubusercontent.com"
private val logger = Logger.withTag("LocalImageFetcher")


@Serializable
data class CS2MapPositionsQuestionJson(
    val image: String,
    val correct_answer: String,
    val incorrect_answers: List<String>
)

/**
 * TODO - refacto is needed to avoid github's rate limit
 * maybe use last commit date or hash
 * TODO - implement pagination if response.truncated is true
 */
class CS2MapPositionsFetcher(
    private val httpClient: HttpClient
) : QandaFetcher {
    override val isEnabled: Boolean
        get() = true

    override suspend fun fetch(fetchConfig: FetchConfig): FetchResult<List<DraftQanda>> {
        val treeApiUrl = buildGithubApiUrl("/git/trees/$GITHUB_BRANCH?recursive=1")
        return try {
            logger.i { "Fetching images from: $treeApiUrl" }
            val gitTreeResponse = httpClient.get(treeApiUrl) {
                header("Accept", "application/vnd.github.v3+json")
                System.getenv("GITHUB_TOKEN")?.let { token -> "token $token" }
            }.body<GitTreeResponse>()
            if (gitTreeResponse.truncated) {
                // todo - implement pagination or fetching individals trees if truncated
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
            val questionFileNode = mapNodes.firstOrNull { it.path.isQuestionFile() }
            if(questionFileNode == null) {
                logger.w { "No question.json file found for map: $mapName. Skipping this map." }
                return@mapNotNull null
            }

            scope.async {
                try {
                    val questionFileUrl = buildGithubRawUrl(questionFileNode.path)
                    logger.i { "Fetching question file for map $mapName from: $questionFileUrl" }
                    val jsonQuestion = httpClient.get(questionFileUrl){
                        header("Accept", "application/vnd.github.v3+json")
                        System.getenv("GITHUB_TOKEN")?.let { token -> "token $token" }
                    }.body<List<CS2MapPositionsQuestionJson>>()
                    jsonQuestion.map { jsonQuestion ->
                        val urlForMap = "$mapName/${jsonQuestion.image}"
                        val imageUrl = buildGithubRawUrl(urlForMap)
                        if (!nodes.any { it.path == urlForMap && it.path.isImageFile() }) {
                            logger.w { "Image specified in question file for map $mapName not found in tree or is not a valid image type: ${jsonQuestion.image}"}
                            return@async null
                        }

                        DraftQanda(
                            question = Question.ImageQuestion(imageUrl = imageUrl),
                            answers = AnswersFactory.createMultipleTextChoices(
                                jsonQuestion.correct_answer,
                                jsonQuestion.incorrect_answers
                            ),
                            category = "CS2 - ${mapName.capitalizeMapName()}"
                        )
                    }
                } catch (e: Exception) {
                    logger.e(e) { "Failed to process question file for map: $mapName (Path: ${questionFileNode.path})" }
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
private fun String.isQuestionFile(): Boolean =
    contains("questions") && extensionFile == "json"

private fun String.isImageFile(): Boolean =
    extensionFile in listOf("jpg", "jpeg", "png")

private fun String.capitalizeMapName() = replaceFirstChar { it.uppercase() }