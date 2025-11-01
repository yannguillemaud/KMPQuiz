package ygmd.kmpquiz.domain.viewModel.fetch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.model.draftqanda.DraftQanda
import ygmd.kmpquiz.domain.result.FetchResult
import ygmd.kmpquiz.domain.usecase.fetch.FetchUseCase
import ygmd.kmpquiz.domain.usecase.fetch.GetFetchersUseCase
import ygmd.kmpquiz.domain.usecase.qanda.SaveQandasUseCase
import ygmd.kmpquiz.domain.viewModel.error.UiError
import ygmd.kmpquiz.domain.viewModel.error.UiEvent
import ygmd.kmpquiz.domain.viewModel.state.UiState

private val logger = Logger.withTag("FetchQandasViewModel")

/**
 * Represents the state of a single fetcher item in the UI.
 */
data class DisplayableFetcher(
    val id: String,
    val name: String,
    val isLoading: Boolean = false,
    val error: UiError? = null,
)


sealed interface FetchIntent {
    data class Fetch(val fetcherId: String) : FetchIntent
}

class FetchQandasViewModel(
    private val getFetchersUseCase: GetFetchersUseCase,
    private val fetchQandaUseCase: FetchUseCase,
    private val saveQandaUseCase: SaveQandasUseCase,
) : ViewModel() {
    val fetchersUiState: StateFlow<UiState<List<DisplayableFetcher>>> =
        getFetchersUseCase.observeFetchers()
            .map { fetchers ->
                UiState.Success(data = fetchers.map { fetcher ->
                    DisplayableFetcher(
                        id = fetcher.id,
                        name = fetcher.name
                    )
                })
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UiState.Loading
            )

    private val _events = MutableSharedFlow<UiEvent>(replay = 10)
    val events = _events.asSharedFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        logger.e(throwable) { "Unhandled exception in ViewModel" }
    }

    /**
     * Processes user intents to interact with the fetchers.
     */
    fun processIntent(intent: FetchIntent) {
        when (intent) {
            is FetchIntent.Fetch -> performFetch(intent.fetcherId)
        }
    }

    private fun performFetch(fetcherId: String) {
        logger.d { "Fetching Q&As from fetcher $fetcherId" }
        viewModelScope.launch(coroutineExceptionHandler) {
            val result = fetchQandaUseCase.execute(fetcherId)
            when (result) {
                is FetchResult.Failure -> _events.emit(
                    UiEvent.Error(UiError.FetchFailed(result))
                )

                is FetchResult.Success<List<DraftQanda>> -> {
                    _events.emit(UiEvent.Success("Fetch success"))
                    result.data.forEach {
                        saveQandaUseCase.save(it)
                    }
                }
            }
        }
    }
}