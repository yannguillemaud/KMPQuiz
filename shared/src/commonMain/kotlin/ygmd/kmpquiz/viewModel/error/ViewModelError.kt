package ygmd.kmpquiz.viewModel.error

sealed class ViewModelError(val errorMessage: String) {
    data class SaveError(val message: String, val cause: Throwable? = null) : ViewModelError(message)
    data class NetworkError(val message: String) : ViewModelError(message)
    data class UnknownError(val message: String, val cause: Throwable) : ViewModelError(message)
}