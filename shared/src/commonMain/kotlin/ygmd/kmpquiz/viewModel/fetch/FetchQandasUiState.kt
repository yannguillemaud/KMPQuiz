package ygmd.kmpquiz.viewModel.fetch

import ygmd.kmpquiz.domain.pojo.InternalQanda

data class FetchQandasUiState(
    val isLoading: Boolean = false,
    val fetchQandas: Map<InternalQanda, DownloadedState> = emptyMap(),
    val error: FetchError? = null,
)

sealed class FetchError(val errorMessage: String) {
    data object MalformedUrlError : FetchError("Malformed URL")
    data object RetryExceeded : FetchError("Retry Exceeding")
    data object ApiResponseError : FetchError("Api Response Error")
}
