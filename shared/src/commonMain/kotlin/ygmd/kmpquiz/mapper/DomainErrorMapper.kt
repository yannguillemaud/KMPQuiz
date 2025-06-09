package ygmd.kmpquiz.mapper

import ygmd.kmpquiz.domain.error.DomainError
import ygmd.kmpquiz.domain.usecase.FetchResult
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

    is DomainError.UnknownError -> ViewModelError.UnknownError(errorMessage, cause)
}

fun <T> FetchResult<T>.toViewModelError(): ViewModelError = when (this) {
    is FetchResult.ApiError -> ViewModelError.NetworkError("API Error $code: $message")
    is FetchResult.RateLimit -> ViewModelError.NetworkError("Rate limit exceeded")
    is FetchResult.Error -> ViewModelError.UnknownError("Network error", throwable)
    is FetchResult.Success -> throw IllegalStateException("Success should not be mapped to error")
}