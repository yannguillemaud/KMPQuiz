package ygmd.kmpquiz.domain.error

sealed class DomainError(
    override val message: String,
    override val cause: Throwable? = null
): Exception(message, cause) {
    // Erreurs du domaine Qanda
    sealed class QandaError(message: String, cause: Throwable? = null) : DomainError(message, cause) {
        data object NotFound : QandaError("Qanda not found") {
            private fun readResolve(): Any = NotFound
        }

        data object AlreadyExists : QandaError("Qanda already exists") {
            private fun readResolve(): Any = AlreadyExists
        }

        data class ValidationError(val field: String, val reason: String) :
            QandaError("Validation failed for field '$field': $reason")
    }

    // Erreurs réseau/fetch
    sealed class NetworkError(message: String, cause: Throwable? = null) : DomainError(message, cause) {
        data class HttpError(val code: Int, val errorMessage: String) :
            NetworkError("HTTP $code: $errorMessage")
        data class ConnectivityError(val errorMessage: String) :
            NetworkError("Connection error: $errorMessage")
        data object RateLimited : NetworkError("Rate limit exceeded") {
            private fun readResolve(): Any = RateLimited
        }

        data class ParseError(val errorMessage: String) :
            NetworkError("Failed to parse response: $errorMessage")
    }

    // Erreurs de persistence
    sealed class PersistenceError(message: String, cause: Throwable? = null) : DomainError(message, cause) {
        data class DatabaseError(val errorMessage: String) :
            PersistenceError("Database error: $errorMessage")
        data object ConnectionFailed : PersistenceError("Failed to connect to database") {
            private fun readResolve(): Any = ConnectionFailed
        }
    }

    // Erreur générique
    data class UnknownError(val errorMessage: String, override val cause: Throwable) :
        DomainError("Unknown error: $errorMessage", cause)
}