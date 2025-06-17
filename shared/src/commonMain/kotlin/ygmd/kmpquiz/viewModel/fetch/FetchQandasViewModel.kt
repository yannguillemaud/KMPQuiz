package ygmd.kmpquiz.viewModel.fetch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.data.repository.service.FetchQanda
import ygmd.kmpquiz.data.repository.service.FetchResult
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.error.toViewModelError
import ygmd.kmpquiz.domain.usecase.GetQandasUseCase
import ygmd.kmpquiz.viewModel.QandaUiState
import ygmd.kmpquiz.viewModel.error.ViewModelError
import ygmd.kmpquiz.viewModel.save.DownloadState.Downloaded
import ygmd.kmpquiz.viewModel.save.DownloadState.NotDownloaded

// États internes pour séparer l'API du state final
private sealed class FetchApiState {
    data object Idle : FetchApiState()
    data object Loading : FetchApiState()
    data object Success : FetchApiState()
    data class Error(val error: ViewModelError) : FetchApiState()
}

class FetchQandasViewModel(
    private val fetchQandaUseCase: FetchQanda,
    getQandasUseCase: GetQandasUseCase,
) : ViewModel() {

    private val _fetchQandas = MutableStateFlow<List<Qanda>>(emptyList())
    private val _fetchState = MutableStateFlow<FetchApiState>(FetchApiState.Idle)

    val fetchState: StateFlow<FetchState> = combine(
        _fetchQandas,
        _fetchState,
        getQandasUseCase.execute(),
    ) { fetchedQandas, apiState, savedQandas ->
        when (apiState) {
            is FetchApiState.Idle -> FetchState.Idle
            is FetchApiState.Loading -> FetchState.Loading
            is FetchApiState.Error -> FetchState.Error(apiState.error)

            is FetchApiState.Success -> {
                if (fetchedQandas.isEmpty()) FetchState.Idle
                else {
                    val qandasWithState = fetchedQandas.map { fetched ->
                        val isAlradySaved = savedQandas.any { saved ->
                            saved.contextKey == fetched.contextKey
                        }

                        QandaUiState(
                            qanda = fetched,
                            downloadState = if (isAlradySaved) Downloaded else NotDownloaded
                        )
                    }

                    FetchState.Success(qandasWithState)
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FetchState.Idle
    )

    init {
        fetchQandas()
    }

    fun fetchQandas() {
        viewModelScope.launch {
            _fetchState.value = FetchApiState.Loading

            when (val result = fetchQandaUseCase.fetch()) {
                is FetchResult.Success -> {
                    _fetchQandas.value = result.data
                    _fetchState.value = FetchApiState.Success
                }

                else -> _fetchState.value = FetchApiState.Error(result.toViewModelError())
            }
        }
    }
}