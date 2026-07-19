package ygmd.kmpquiz.presentation.viewModel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.core.domain.event.Event
import ygmd.kmpquiz.core.domain.fetcher.FetcherState
import ygmd.kmpquiz.core.usecase.category.CategoryUseCase
import ygmd.kmpquiz.core.usecase.fetcher.GetFetchersUseCase
import ygmd.kmpquiz.core.usecase.qanda.DownloadQandasUseCase
import ygmd.kmpquiz.core.usecase.qanda.GetQandaUseCase
import ygmd.kmpquiz.core.usecase.quiz.GetQuizUseCase

data class HomeContentState(
    val totalCategoriesCount: Int = 0,
    val totalQandasCount: Int = 0,
    val totalQuizCount: Int = 0,
    val sources: Map<String, FetcherUiState> = emptyMap(),
){
    val isDownloading = sources.any { it.value.isDownloading }
}

/**
 * Render-ready state of one question source.
 *
 * [status] flattens the domain [FetcherState] into the four UI states the source row can
 * paint (Ready / Syncing / Updated / Error), giving the "Updated" (green) dot a real data
 * path. [isDownloading] stays derived so the aggregate [HomeContentState.isDownloading]
 * keeps working unchanged.
 */
data class FetcherUiState(
    val name: String,
    val status: FetcherStatus,
    val error: String? = null,
) {
    val isDownloading: Boolean get() = status == FetcherStatus.Syncing
}

/** UI-facing status of a question source, mapped 1:1 from [FetcherState]. */
enum class FetcherStatus { Ready, Syncing, Updated, Error }

sealed interface DownloadIntent {
    data class Fetch(val fetcherId: String) : DownloadIntent
    object FetchAll : DownloadIntent
}

class HomeViewModel(
    private val categoryUseCase: CategoryUseCase,
    private val getQandaUseCase: GetQandaUseCase,
    private val getQuizUseCase: GetQuizUseCase,
    private val getFetchersUseCase: GetFetchersUseCase,
    private val downloadQandasUseCase: DownloadQandasUseCase,
) : ViewModel() {
    private val _events = Channel<Event>()
    val events = _events.receiveAsFlow()


    val contentFlow = combine(
        categoryUseCase.observeCategories(),
        getQuizUseCase.observeQuizzes(),
        getQandaUseCase.observeQandas(),
        getFetchersUseCase.observeFetchers()
    ) { categories, quizzes, qandas, fetchers ->
        HomeContentState(
            totalCategoriesCount = categories.size,
            totalQuizCount = quizzes.size,
            totalQandasCount = qandas.size,
            sources = fetchers.associate { fetcher ->
                fetcher.id to FetcherUiState(
                    name = fetcher.name,
                    status = when (fetcher.state) {
                        FetcherState.Idle -> FetcherStatus.Ready
                        FetcherState.Fetching -> FetcherStatus.Syncing
                        FetcherState.Success -> FetcherStatus.Updated
                        is FetcherState.Error -> FetcherStatus.Error
                    },
                    error = (fetcher.state as? FetcherState.Error)?.message
                )
            }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HomeContentState()
    )

    fun processIntent(intent: DownloadIntent) {
        when (intent) {
            is DownloadIntent.Fetch -> viewModelScope.launch { fetch(intent.fetcherId) }
            DownloadIntent.FetchAll -> viewModelScope.launch { fetchAll() }
        }
    }

    private suspend fun fetch(fetcherId: String) {
        downloadQandasUseCase(fetcherId).fold(
            onSuccess = { _events.send(Event.ShowSnackbar("Qandas fetched")) },
            onFailure = { _events.send(Event.ShowSnackbar("Error while downloading.")) }
        )
    }

    private suspend fun fetchAll() {
        downloadQandasUseCase()
    }
}