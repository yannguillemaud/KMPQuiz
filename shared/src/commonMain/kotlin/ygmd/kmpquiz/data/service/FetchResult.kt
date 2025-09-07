package ygmd.kmpquiz.data.service

import kotlin.time.Duration

sealed class FetchResult<out T> {
    data class Success<T>(val data: T) : FetchResult<T>()

    data class Failure(
        val type: FailureType,
        val message: String,
        val retryAfter: Duration? = null,
        val cause: Throwable? = null
    ) : FetchResult<Nothing>()
}

enum class FailureType {
    RATE_LIMIT,    // Trop de requêtes, retryAfter sera rempli
    API_ERROR,     // Erreur 4xx/5xx ou response_code != 0
    NETWORK_ERROR,  // Problème réseau/parsing,
    SERVER_ERROR,
    UNKNOWN_ERROR,
    ERROR,
}