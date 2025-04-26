package ygmd.kmpquiz.domain.viewModel.state

import ygmd.kmpquiz.domain.viewModel.error.UiError

sealed interface UiState<out T> {
    /**
     * Represents the initial loading state where no data is available yet.
     */
    data object Loading : UiState<Nothing>

    /**
     * Represents a success state where data is available.
     * Can also indicate if a data refresh is in progress.
     *
     * @param data The successfully fetched data.
     * @param isRefreshing True if the current data is being displayed while a refresh is in progress, false otherwise.
     */
    data class Success<T>(
        val data: T,
        val isRefreshing: Boolean = false,
    ) : UiState<T>

    /**
     * Represents an error state.
     *
     * @param currentValue The current (potentially stale) data that was available before the error occurred, if applicable.
     * @param error The error that occurred.
     */
    data class Error<T>(
        val currentValue: T?,
        val error: UiError
    ) : UiState<T>
}

/**
 * Returns the data if the state is `Success`, or the current value if the state is `Error` and it exists, otherwise null.
 * Returns nothing for `Loading`.
 */
inline fun <reified T> UiState<T>.getOrNull(): T? = when (this) {
    is UiState.Success -> data
    is UiState.Error -> currentValue
    else -> null
}

/**
 * Returns the data if the state is `Success`, otherwise the default value.
 * For `Error`, it returns the existing `currentValue` if not null, otherwise the default value.
 */
fun <T> UiState<T>.getOrDefault(default: T): T = when (this) {
    is UiState.Success -> data
    is UiState.Error -> currentValue ?: default
    else -> default
}

/**
 * Transforms the data within a `Success` state.
 * If the state is `Success`, the new state preserves the `isRefreshing` flag.
 * If the state is `Error`, it optionally transforms `currentValue` as well.
 */
inline fun <T, R> UiState<T>.map(transform: (T) -> R): UiState<R> = when (this) {
    is UiState.Success -> UiState.Success(transform(data), isRefreshing)
    is UiState.Loading -> UiState.Loading
    is UiState.Error -> UiState.Error(currentValue?.let(transform), error)
}
