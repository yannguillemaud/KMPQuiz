package ygmd.kmpquiz.core.domain.errors

sealed class FetchError : Exception() {
    class RateLimitError : FetchError()
    class ApiError(val responseCode: Int) : FetchError()
    class NetworkError : FetchError()
    class ServerError : FetchError()
    class UnknownError : FetchError()
}