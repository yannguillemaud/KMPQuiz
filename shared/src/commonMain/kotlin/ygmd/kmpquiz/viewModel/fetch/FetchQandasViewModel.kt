package ygmd.kmpquiz.viewModel.fetch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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

private val logger = Logger.withTag(FetchQandasViewModel::class.java.name)

sealed interface FetchIntentAction {
    data object Fetch : FetchIntentAction
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

    init {
        fetchQandas()
    }

    private val _fetchedQandas = getFetchQandasUseCase.observeFetched()
    private val _savedQandas = getSavedQandasUseCase.observeSaved()

    val fetchState: StateFlow<UiState<Map<String, List<DraftQanda>>>> = combine(
        _fetchedQandas,
        _savedQandas,
    ) { fetchedQandas, savedQandas ->
        logger.i { "Updating state" }
        val savedContextKeys = savedQandas.map { it.contextKey }.toSet()
        val filteredQandas = fetchedQandas
            .filter { it.contextKey !in savedContextKeys }
            .groupBy { it.category }
        when {
            filteredQandas.isEmpty() -> UiState.Error(
                error = UiError.EmptyFetch,
                currentValue = null
            )

            else -> UiState.Success(filteredQandas)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UiState.Loading
    )

    fun onIntentAction(fetchIntentAction: FetchIntentAction) = when (fetchIntentAction) {
        is FetchIntentAction.Fetch -> fetchQandas()
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
                _events.tryEmit(
                    UiEvent.Success("Fetched ${fetchedQandas.size} qandas")
                )
            }
        )
    }
}