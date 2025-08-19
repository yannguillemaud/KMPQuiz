package ygmd.kmpquiz.viewModel.fetch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ygmd.kmpquiz.application.manager.FetchThrottleManager
import ygmd.kmpquiz.application.usecase.fetch.FetchQandasUseCase
import ygmd.kmpquiz.application.usecase.fetch.GetFetchQandasUseCase
import ygmd.kmpquiz.application.usecase.qanda.GetQandaUseCase
import ygmd.kmpquiz.domain.repository.DraftQanda
import ygmd.kmpquiz.viewModel.error.SnackbarAction
import ygmd.kmpquiz.viewModel.error.UiError
import ygmd.kmpquiz.viewModel.error.UiEvent
import ygmd.kmpquiz.viewModel.state.UiState
import ygmd.kmpquiz.viewModel.state.getOrDefault
import ygmd.kmpquiz.viewModel.state.map

private val logger = Logger.withTag(FetchQandasViewModel::class.java.name)

sealed interface FetchIntentAction {
    data object Fetch : FetchIntentAction
    data class UpdateState(val category: String) : FetchIntentAction
}

class FetchQandasViewModel(
    private val fetchQandaUseCase: FetchQandasUseCase,
    getFetchQandasUseCase: GetFetchQandasUseCase,
    getSavedQandasUseCase: GetQandaUseCase,
) : ViewModel() {
    private val fetchThrottleManager = FetchThrottleManager(scope = viewModelScope)
    val canExecuteFetch = fetchThrottleManager.canExecute

    private val _events = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    private val _fetchState: MutableStateFlow<UiState<Map<String, List<DraftQanda>>>> =
        MutableStateFlow(UiState.Loading)

    init {
        fetchQandas()
    }

    val fetchState: StateFlow<UiState<Map<String, List<DraftQanda>>>> = combine(
        _fetchState,
        getFetchQandasUseCase.observeFetched(),
        getSavedQandasUseCase.observeSaved(),
    ) { state, fetchedQandas, savedQandas ->
        val currentFetched = _fetchState.value.getOrDefault(emptyMap())
        val savedContextKeys = savedQandas.map { it.contextKey }.toSet()
        val filteredFetchedQandas = fetchedQandas
            .filter { it.contextKey !in savedContextKeys }
            .groupBy { it.category }
        val newFetch = currentFetched + filteredFetchedQandas
        when {
            newFetch.isEmpty() -> UiState.Error(
                currentValue = currentFetched,
                error = UiError.EmptyFetch
            )
            else -> UiState.Success(filteredFetchedQandas)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UiState.Loading
    )

    fun onIntentAction(fetchIntentAction: FetchIntentAction) = when (fetchIntentAction) {
        is FetchIntentAction.Fetch -> fetchQandas()
        is FetchIntentAction.UpdateState -> removeFetchedCategory(fetchIntentAction.category)
    }

    private fun removeFetchedCategory(category: String) {
        viewModelScope.launch {
            _fetchState.update { state ->
                state.map { current ->
                    current.filterKeys { it != category }
                }
            }
        }
    }

    private fun fetchQandas() {
        viewModelScope.launch {
            val wasExecuted = fetchThrottleManager.executeIfAllowed {
                performFetch()
            }

            if (!wasExecuted) {
                logger.w { "Fetch request throttled" }
            }
        }
    }

    private suspend fun performFetch() {
        fetchQandaUseCase.fetch().fold(
            onFailure = { throwable ->
                logger.e(throwable) { "Failed to fetch qandas" }
                _fetchState.value = UiState.Error(
                    currentValue = _fetchState.value,
                    error = UiError.FetchFailed
                )
                _events.tryEmit(
                    UiEvent.Error(
                        message = UiError.FetchFailed.message,
                        action = SnackbarAction(
                            label = "Retry",
                            action = { fetchQandas() }
                        )
                    )
                )
            },
            onSuccess = { fetchedQandas ->
                logger.i { "Fetched ${fetchedQandas.size} qandas" }
                _fetchState.update { state ->
                    state.map { current ->
                        current + fetchedQandas.groupBy { it.category }
                    }
                }
                _events.tryEmit(
                    UiEvent.Success("Fetched ${fetchedQandas.size} qandas")
                )
            }
        )
    }
}