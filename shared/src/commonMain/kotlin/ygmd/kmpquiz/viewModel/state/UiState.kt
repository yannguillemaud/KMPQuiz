package ygmd.kmpquiz.viewModel.state

import ygmd.kmpquiz.viewModel.error.UiError

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error<T>(val currentValue: T?, val error: UiError) : UiState<Nothing>
}

val <T> UiState<T>.isLoading get() = this is UiState.Loading
val <T> UiState<T>.isSuccess get() = this is UiState.Success
val <T> UiState<T>.isError get() = this is UiState.Error<*>

fun <T> UiState<T>.getOrNull(): T? = when(this){
    is UiState.Success -> data
    else -> null
}

fun <T> UiState<T>.getOrDefault(default: T): T = when(this){
    is UiState.Success -> data
    else -> default
}

fun <T> UiState<T>.errorOrNull(): UiError? = when(this){
    is UiState.Error<*> -> error
    else -> null
}

inline fun <T, R> UiState<T>.map(transform: (T) -> R): UiState<R> = when(this) {
    is UiState.Success -> UiState.Success(transform(data))
    is UiState.Loading -> UiState.Loading
    is UiState.Error<*> -> this
}