package ygmd.kmpquiz.domain.viewModel.coordinator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import ygmd.kmpquiz.domain.entities.qanda.Answers
import ygmd.kmpquiz.domain.entities.qanda.Metadata
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.entities.qanda.Question
import ygmd.kmpquiz.domain.repository.DraftQanda
import ygmd.kmpquiz.domain.viewModel.error.UiError
import ygmd.kmpquiz.domain.viewModel.fetch.FetchQandasViewModel
import ygmd.kmpquiz.domain.viewModel.save.SavedQandasViewModel
import ygmd.kmpquiz.domain.viewModel.state.errorOrNull
import ygmd.kmpquiz.domain.viewModel.state.getOrDefault
import ygmd.kmpquiz.domain.viewModel.state.isLoading

/**
 * unified state for the Fetch screen combining fetch and save responsabilities
 */
data class FetchAndSavedState(
    // map<contextkey, qanda>
    val qandaByContextKey: Map<String, UiQanda> = emptyMap(),
    val isLoading: Boolean = false,
    val error: UiError? = null,
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
    val fetchAndSavedState: StateFlow<FetchAndSavedState> = combine(
        fetchQandasViewModel.fetchState,
        savedQandasViewModel.saveState,
    ) { fetchState, saveState ->
        val fetched = fetchState
            .getOrDefault(emptyList())
            .map { it.toUiQanda() }
        val saved = saveState
            .savedQandas
            .values
            .flatten()
            .map { it.toUiQanda() }

        FetchAndSavedState(
            qandaByContextKey = (fetched + saved).associateBy { it.contextKey },
            isLoading = fetchState.isLoading || saveState.isLoading,
            error = fetchState.errorOrNull() ?: saveState.error,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        FetchAndSavedState()
    )
}

private fun DraftQanda.toUiQanda(): UiQanda = UiQanda(
    contextKey,
    question,
    answers,
    Metadata(category = category, difficulty = null),
    false
)

private fun Qanda.toUiQanda(): UiQanda = UiQanda(
    contextKey,
    question,
    answers,
    metadata,
    true
)

data class UiQanda(
    val contextKey: String,
    val question: Question,
    val answers: Answers,
    val metadata: Metadata,
    val isSaved: Boolean,
)