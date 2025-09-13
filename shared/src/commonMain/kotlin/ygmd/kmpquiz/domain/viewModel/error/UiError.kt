package ygmd.kmpquiz.domain.viewModel.error


sealed interface UiError {
    val message: String
    val canRetry: Boolean

    data object FetchFailed: UiError {
        override val message: String = "Failed to fetch qandas"
        override val canRetry: Boolean = true
    }

    data object EmptyFetch: UiError {
        override val message: String = "No qandas to fetch"
        override val canRetry: Boolean = true
    }

    data object EmptyQuiz: UiError {
        override val message: String = "No qandas to quiz on"
        override val canRetry: Boolean = false
    }

    data object SaveFailed: UiError {
        override val message: String = "Failed to save qandas"
        override val canRetry: Boolean = false
    }
}

sealed interface UiEvent {
    data class Success(val message: String): UiEvent
    data class Error(val message: String, val action: SnackbarAction?): UiEvent
}

data class SnackbarAction(
    val label: String,
    val action: () -> Unit,
)