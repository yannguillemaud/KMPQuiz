package ygmd.kmpquiz.viewModel.error

/* TODO refacto */
sealed class ViewModelError(val errorMessage: String) {
    data class SaveError(val message: String, val cause: Throwable? = null) : ViewModelError(message)
    data class NetworkError(val message: String) : ViewModelError(message)
    data class UnknownError(val message: String, val cause: Throwable) : ViewModelError(message)
}

fun Throwable.toViewModelError() = ViewModelError.UnknownError(message ?: "Error", this)