package ygmd.kmpquiz.viewModel.fetch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.fetch.FetchResult
import ygmd.kmpquiz.domain.fetch.FetchResult.Success
import ygmd.kmpquiz.domain.fetch.OpenTriviaFetchQanda
import ygmd.kmpquiz.viewModel.QandaUiState
import ygmd.kmpquiz.viewModel.error.ViewModelError
import ygmd.kmpquiz.viewModel.save.DownloadState
import ygmd.kmpquiz.viewModel.save.SaveQandasState

class FetchQandasViewModel(
    private val fetchQandasUseCase: OpenTriviaFetchQanda,
) : ViewModel() {

    private val _fetchedState = MutableStateFlow<FetchState>(FetchState.Idle)
    private val _savedState = MutableStateFlow(SaveQandasState())

    val fetchState = combine(_fetchedState, _savedState) { fetchState, saveState ->
        when (fetchState) {
            is FetchState.Success -> {
                println("DEBUG: Success state with ${fetchState.availableQandas.size} qandas")

                // Création de la liste complète avec état de téléchargement mis à jour
                val updatedQandas = fetchState.availableQandas.map { qandaUiState ->
                    val isSaved = saveState.savedQandas.any { saved ->
                        saved.contentKey == qandaUiState.qanda.contentKey
                    }
                    qandaUiState.copy(
                        downloadState = if (isSaved) DownloadState.Downloaded else DownloadState.NotDownloaded
                    )
                }

                FetchState.Success(
                    qandasWithState = updatedQandas // Assurez-vous que cette propriété existe et est peuplée
                )
            }

            is FetchState.Loading -> {
                println("DEBUG: Loading state")
                FetchState.Loading
            }

            is FetchState.Error -> {
                println("DEBUG: Error state: ${fetchState.error.errorMessage}")
                fetchState.copy(
                    lastSuccessfulData = saveState.savedQandas.map {
                        QandaUiState(it, DownloadState.Downloaded)
                    }
                )
            }

            FetchState.Idle -> {
                println("DEBUG: Idle state")
                FetchState.Idle
            }
        }
    }

    init {
        fetchQandas()
    }

    fun fetchQandas() {
        viewModelScope.launch {
            _fetchedState.value = FetchState.Loading

            try {
                when (val result = fetchQandasUseCase.fetch()) {
                    is Success -> {
                        _fetchedState.value = FetchState.Success(
                            result.data.map { fetched ->
                                QandaUiState(
                                    qanda = fetched,
                                    downloadState = _savedState.value.savedQandas.firstOrNull { saved ->
                                        saved.contentKey == fetched.contentKey()
                                    }?.let { DownloadState.Downloaded }
                                        ?: DownloadState.NotDownloaded
                                )
                            }
                        )
                    }

                    is FetchResult.Error,
                    is FetchResult.RateLimit,
                    is FetchResult.ApiError -> {
                        _fetchedState.value = FetchState.Error(
                            ViewModelError.FetchError("Retry exceeded"),
                            lastSuccessfulData = _savedState.value.savedQandas.map {
                                QandaUiState(
                                    qanda = it,
                                    downloadState = DownloadState.Downloaded
                                )
                            },
                        )
                    }
                }
            } catch (e: Exception) {
                _fetchedState.value = FetchState.Error(
                    ViewModelError.UnknownError("Unknown error", e)
                )
            }
        }
    }
}