package ygmd.kmpquiz.domain.error

import ygmd.kmpquiz.data.repository.service.FailureType.API_ERROR
import ygmd.kmpquiz.data.repository.service.FailureType.ERROR
import ygmd.kmpquiz.data.repository.service.FailureType.NETWORK_ERROR
import ygmd.kmpquiz.data.repository.service.FailureType.RATE_LIMIT
import ygmd.kmpquiz.data.repository.service.FetchResult
import ygmd.kmpquiz.viewModel.error.ViewModelError

fun DomainError.toViewModelError(): ViewModelError = when (this) {
    is DomainError.NetworkError.HttpError -> ViewModelError.NetworkError("HTTP $code: $errorMessage")
    is DomainError.NetworkError.ConnectivityError -> ViewModelError.NetworkError("Connection failed: $errorMessage")
    is DomainError.NetworkError.RateLimited -> ViewModelError.NetworkError("Rate limit exceeded")
    is DomainError.NetworkError.ParseError -> ViewModelError.NetworkError("Parse error: $errorMessage")

    is DomainError.QandaError.NotFound -> ViewModelError.SaveError("Qanda not found")
    is DomainError.QandaError.AlreadyExists -> ViewModelError.SaveError("Qanda already exists")
    is DomainError.QandaError.ValidationError -> ViewModelError.SaveError("Validation error: $reason")

    is DomainError.PersistenceError.DatabaseError -> ViewModelError.SaveError("Database error: $errorMessage")
    is DomainError.PersistenceError.ConnectionFailed -> ViewModelError.SaveError("Database connection failed")

    is DomainError.CronError.CronNotExists -> ViewModelError.SaveError("Cron does not not exists")

    is DomainError.UnknownError -> ViewModelError.UnknownError(message, cause)
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