package ygmd.kmpquiz.domain.viewModel.fetch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.manager.FetchThrottleManager
import ygmd.kmpquiz.domain.usecase.fetch.FetchQandasUseCase
import ygmd.kmpquiz.domain.usecase.fetch.GetFetchQandasUseCase
import ygmd.kmpquiz.domain.viewModel.error.SnackbarAction
import ygmd.kmpquiz.domain.viewModel.error.UiError
import ygmd.kmpquiz.domain.viewModel.error.UiEvent
import ygmd.kmpquiz.domain.viewModel.state.UiState

private val logger = Logger.withTag(FetchQandasViewModel::class.java.name)

sealed interface FetchIntentAction {
    data object Fetch : FetchIntentAction
}

class FetchQandasViewModel(
    private val fetchQandaUseCase: FetchQandasUseCase,
    getFetchQandasUseCase: GetFetchQandasUseCase,
) : ViewModel() {
    private val fetchThrottleManager = FetchThrottleManager(scope = viewModelScope)
    val canExecuteFetch = fetchThrottleManager.canExecute

    private val _events = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    init {
        fetchQandas()
    }

    val fetchState = getFetchQandasUseCase.observeFetched()
        .map { UiState.Success(it) }
        .stateIn(
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