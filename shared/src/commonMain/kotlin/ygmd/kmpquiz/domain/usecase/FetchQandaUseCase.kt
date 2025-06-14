package ygmd.kmpquiz.domain.usecase

import ygmd.kmpquiz.domain.pojo.qanda.InternalQanda
import kotlin.time.Duration

interface FetchQandaUseCase {
    sealed class FetchResult<out T> {
        data class Success<T>(val data: T) : FetchResult<T>()

        data class Failure(
            val type: FailureType,
            val message: String,
            val retryAfter: Duration? = null,
            val cause: Throwable? = null
        ) : FetchResult<Nothing>()
    }

    suspend fun fetch(): FetchResult<List<InternalQanda>>
}

enum class FailureType {
    RATE_LIMIT,    // Trop de requêtes, retryAfter sera rempli
    API_ERROR,     // Erreur 4xx/5xx ou response_code != 0
    NETWORK_ERROR  // Problème réseau/parsing
}