package ygmd.kmpquiz.domain.viewModel.error

import ygmd.kmpquiz.domain.result.FetchResult


sealed interface UiError {
    val message: String
    val canRetry: Boolean

    data class FetchFailed(val failure: FetchResult.Failure? = null): UiError {
        override val message: String = "Failed to fetch qandas. ${failure?.message}"
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

    data object LoadQuizFailed: UiError {
        override val message: String = "Failed to load quiz"
        override val canRetry: Boolean = false
    }

    data object LoadQandaFailed: UiError {
        override val message: String = "Failed to load qanda"
        override val canRetry: Boolean = false
    }

    data object SaveFailed: UiError {
        override val message: String = "Failed to save"
        override val canRetry: Boolean = false
    }

    data class SaveQandaFailed(
        override val message: String
    ): UiError {
        override val canRetry: Boolean = false
    }

    data class SaveQandasFailed(
        override val message: String
    ): UiError {
        override val canRetry: Boolean = false
    }

    data object LoadCategoryFailed: UiError {
        override val message: String
            get() = "Load category failed"
        override val canRetry: Boolean = false
    }


}

sealed interface UiEvent {
    data class Success(val message: String): UiEvent
    data class Error(
        val error: UiError,
        val action: SnackbarAction? = null,
    ): UiEvent
}

data class SnackbarAction(
    val label: String,
    val action: () -> Unit,
)