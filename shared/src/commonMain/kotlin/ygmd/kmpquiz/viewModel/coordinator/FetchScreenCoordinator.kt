package ygmd.kmpquiz.viewModel.coordinator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import ygmd.kmpquiz.domain.repository.DraftQanda
import ygmd.kmpquiz.viewModel.error.UiError
import ygmd.kmpquiz.viewModel.fetch.FetchQandasViewModel
import ygmd.kmpquiz.viewModel.save.SavedQandasViewModel
import ygmd.kmpquiz.viewModel.state.errorOrNull
import ygmd.kmpquiz.viewModel.state.getOrDefault
import ygmd.kmpquiz.viewModel.state.isLoading

/**
 * unified state for the Fetch screen combining fetch and save responsabilities
 */
data class FetchScreenState(
    // Fetch state
    val qandasByCategory: Map<String, List<DraftQanda>> = emptyMap(),
    val isFetching: Boolean = false,
    val fetchError: UiError? = null,
    val canRefresh: Boolean = true,

    // Save state
    val isSaving: Boolean = false,
    val saveError: UiError? = null,
)

/**
 * coordinator that orchestrates FetchQandasViewModel and SaveQandasViewModel
 * to provide a unified interface for the Fetch screen
 *
 * -> this solves the problem of having multiple ViewModels in a single screen
 * while maintaining separation of concerns
 */
class FetchScreenCoordinator(
    fetchQandasViewModel: FetchQandasViewModel,
    savedQandasViewModel: SavedQandasViewModel,
) : ViewModel() {

    /**
     * unified state [fetch, save]
     */
    val state: StateFlow<FetchScreenState> = combine(
        fetchQandasViewModel.fetchState,
        fetchQandasViewModel.canExecuteFetch,
        savedQandasViewModel.saveState,
    ) { fetchState, canRefresh, saveState ->
        FetchScreenState(
            qandasByCategory = fetchState.getOrDefault(emptyMap()),
            isFetching = fetchState.isLoading,
            fetchError = fetchState.errorOrNull(),
            canRefresh = canRefresh,
            isSaving = saveState.isLoading,
            saveError = saveState.error,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        FetchScreenState()
    )
}