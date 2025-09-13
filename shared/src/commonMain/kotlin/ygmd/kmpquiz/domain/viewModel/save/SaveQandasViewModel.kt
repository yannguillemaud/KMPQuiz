package ygmd.kmpquiz.domain.viewModel.save

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.repository.DraftQanda
import ygmd.kmpquiz.domain.usecase.fetch.DeleteFetchQandasUseCase
import ygmd.kmpquiz.domain.usecase.qanda.DeleteQandasUseCase
import ygmd.kmpquiz.domain.usecase.qanda.GetQandaUseCase
import ygmd.kmpquiz.domain.usecase.qanda.SaveQandasUseCase
import ygmd.kmpquiz.domain.viewModel.error.UiError
import ygmd.kmpquiz.domain.viewModel.error.UiEvent

sealed interface PersistanceIntent {
    data class SaveAll(val qandas: List<DraftQanda>) : PersistanceIntent
    data class DeleteQanda(val qandaId: String) : PersistanceIntent
    data class DeleteByCategory(val category: String) : PersistanceIntent
    data object ClearError : PersistanceIntent
}

data class SaveUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val savedQandas: Map<String, List<Qanda>> = emptyMap(),
)

class SavedQandasViewModel(
    private val saveQandaUseCase: SaveQandasUseCase,
    private val deleteQandasUseCase: DeleteQandasUseCase,
    private val deleteFetchQandasUseCase: DeleteFetchQandasUseCase,
    private val getQandaUseCase: GetQandaUseCase,
) : ViewModel() {

    private val _saveState = MutableStateFlow(SaveUiState())
    val saveState: StateFlow<SaveUiState> = _saveState.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    init {
        loadSavedQandas()
    }

    private fun loadSavedQandas() {
        viewModelScope.launch {
            _saveState.update { it.copy(isLoading = true, error = null) }
            getQandaUseCase.observeSaved().collect { qandas ->
                _saveState.update {
                    it.copy(
                        isLoading = false,
                        savedQandas = qandas.groupBy { it.metadata.category }
                    )
                }
            }
        }
    }

    fun processIntent(persistanceIntent: PersistanceIntent) {
        return when (persistanceIntent) {
            is PersistanceIntent.DeleteByCategory -> {
                deleteByCategory(persistanceIntent.category)
            }

            is PersistanceIntent.DeleteQanda -> {
                deleteQanda(persistanceIntent.qandaId)
            }

            is PersistanceIntent.SaveAll -> {
                saveAll(persistanceIntent.qandas)
            }

            PersistanceIntent.ClearError -> {
                clearError()
            }
        }
    }

    private fun clearError() {
        _saveState.update { it.copy(error = null) }
    }

    private fun saveAll(qandas: List<DraftQanda>) {
        viewModelScope.launch {
            _saveState.update { it.copy(isLoading = true, error = null) }
            saveQandaUseCase.saveAll(qandas)
                .fold(
                    onFailure = { error ->
                        _saveState.update {
                            it.copy(isLoading = false, error = UiError.SaveFailed)
                        }
                    },
                    onSuccess = {
                        handleSuccessfullSave(qandas)
                    }
                )
        }
    }

    private fun deleteQanda(qandaId: String) {
        viewModelScope.launch {
            deleteQandasUseCase.deleteById(qandaId)
        }
    }

    private fun deleteByCategory(category: String) {
        viewModelScope.launch {
            deleteQandasUseCase.deleteAllByCategory(category)
            _events.tryEmit(UiEvent.Success("Deleted all qandas from $category"))
        }
    }

    private suspend fun handleSuccessfullSave(qandas: List<DraftQanda>) {
        deleteFetchQandasUseCase.delete(qandas)
        _events.tryEmit(UiEvent.Success("Saved ${qandas.size} qandas"))
    }
}