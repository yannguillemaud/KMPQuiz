package ygmd.kmpquiz.viewModel.fetch

import ygmd.kmpquiz.viewModel.QandaStatus
import ygmd.kmpquiz.viewModel.QandaUiState
import ygmd.kmpquiz.viewModel.error.ViewModelError

sealed class FetchState {
    data object Idle : FetchState()
    data object Loading : FetchState()

    data class Success(
        val qandasWithState: List<QandaUiState>
    ) : FetchState() {
        /**
         * Groupement par status
         */
        val qandasByStatus: Map<QandaStatus, List<QandaUiState>> by lazy {
            qandasWithState.groupBy { it.status }
        }

        /**
         * Fast access aux groupes
         */
        val availableQandas: List<QandaUiState>
            get() = qandasByStatus[QandaStatus.AVAILABLE].orEmpty()

        val savedQandas: List<QandaUiState>
            get() = qandasByStatus[QandaStatus.SAVED].orEmpty()

        val downloadingQandas: List<QandaUiState>
            get() = qandasByStatus[QandaStatus.DOWNLOADING].orEmpty()

        val errorQandas: List<QandaUiState>
            get() = qandasByStatus[QandaStatus.ERROR].orEmpty()

        val qandasByCategory: Map<String, List<QandaUiState>>
            get() = qandasByStatus.values.flatten().groupBy { it.qanda.category }

        /**
         * Stats
         */
        val totalCount: Int get() = qandasWithState.size
        val availableCount: Int get() = availableQandas.size
        val savedCount: Int get() = savedQandas.size
        val downloadingCount: Int get() = downloadingQandas.size
        val errorCount: Int get() = errorQandas.size

        /**
         * Etats pour l'UI
         */
        val hasData: Boolean get() = totalCount > 0
        val isEmpty: Boolean get() = totalCount == 0
        val canSaveAll: Boolean get() = availableQandas.isNotEmpty()
        val showProgress: Boolean get() = downloadingCount > 0
    }

    data class Error(
        val error: ViewModelError,
        val lastSuccessfulData: List<QandaUiState> = emptyList()
    ) : FetchState() {
        val hasPartialData: Boolean get() = lastSuccessfulData.isNotEmpty()
    }
}