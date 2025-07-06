package ygmd.kmpquiz.viewModel.fetch

import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.viewModel.error.ViewModelError

sealed interface FetchState {
    data object Idle : FetchState
    data object Loading : FetchState

    data class Success(
        val qandas: List<Qanda>
    ) : FetchState

    data class Error(
        val error: ViewModelError,
        val lastSuccessfullData: List<Qanda> = emptyList()
    ) : FetchState
}