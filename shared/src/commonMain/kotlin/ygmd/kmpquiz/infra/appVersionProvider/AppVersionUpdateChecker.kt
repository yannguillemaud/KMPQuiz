package ygmd.kmpquiz.infra.appVersionProvider

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import ygmd.kmpquiz.domain.appVersionProvider.AppVersionProvider
import ygmd.kmpquiz.domain.appVersionProvider.RepositoryOrigineConst
import ygmd.kmpquiz.domain.git.GithubReleaseDetails

private val logger: Logger = Logger.withTag("AppVersionUpdateChecker")

class AppVersionUpdateChecker(
    private val httpClient: HttpClient,
    private val appVersionProvider: AppVersionProvider
) {
    suspend fun isUpdateAvailable(): Boolean {
        return try {
            val latestRelease = httpClient.get(RepositoryOrigineConst.LATEST_REPOSITORY_URL)
                .body<GithubReleaseDetails>()
            val currentVersioName = appVersionProvider.getAppVersionName()
            val latestVersionName = latestRelease.name.removePrefix("v")
            latestVersionName != currentVersioName
        } catch (e: Exception) {
            logger.e(e) { "Error checking for update" }
            false
        }
    }
}