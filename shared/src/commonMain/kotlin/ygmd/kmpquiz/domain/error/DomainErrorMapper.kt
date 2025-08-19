package ygmd.kmpquiz.domain.error

import ygmd.kmpquiz.data.service.FailureType.API_ERROR
import ygmd.kmpquiz.data.service.FailureType.ERROR
import ygmd.kmpquiz.data.service.FailureType.NETWORK_ERROR
import ygmd.kmpquiz.data.service.FailureType.RATE_LIMIT
import ygmd.kmpquiz.data.service.FetchResult
import ygmd.kmpquiz.viewModel.error.ViewModelError
import ygmd.kmpquiz.viewModel.error.ViewModelError.SaveError

fun DomainError.toViewModelError(): ViewModelError = when (this) {
    is DomainError.NetworkError.HttpError -> ViewModelError.NetworkError("HTTP $code: $errorMessage")
    is DomainError.NetworkError.ConnectivityError -> ViewModelError.NetworkError("Connection failed: $errorMessage")
    is DomainError.NetworkError.RateLimited -> ViewModelError.NetworkError("Rate limit exceeded")
    is DomainError.NetworkError.ParseError -> ViewModelError.NetworkError("Parse error: $errorMessage")

    is DomainError.QandaError.NotFound -> SaveError("Qanda not found")
    is DomainError.QandaError.AlreadyExists -> SaveError("Qanda already exists")
    is DomainError.QandaError.ValidationError -> SaveError("Validation error: $reason")

    is DomainError.PersistenceError.DatabaseError -> SaveError("Database error: $errorMessage")
    is DomainError.PersistenceError.ConnectionFailed -> SaveError("Database connection failed")

    is DomainError.CronError.CronNotExists -> SaveError("Cron does not not exists")

    is DomainError.UnknownError -> ViewModelError.UnknownError(message, cause)
    is DomainError.QuizSessionError.EmptyQuizSession -> TODO()
    is DomainError.QuizSessionError.QandasNotFoundForSession -> TODO()
}

fun <T> FetchResult<T>.toViewModelError(): ViewModelError = when (this) {
    is FetchResult.Success -> throw IllegalStateException("Success should not be mapped to error")
    is FetchResult.Failure -> when(this.type){
        RATE_LIMIT ->  ViewModelError.NetworkError("Rate limit exceeded")
        API_ERROR -> ViewModelError.NetworkError("API Error: ${this.message}")
        NETWORK_ERROR -> ViewModelError.UnknownError("Network error", this.cause ?: RuntimeException())
        ERROR -> ViewModelError.UnknownError("Unknown error", this.cause ?: RuntimeException())
    }
}