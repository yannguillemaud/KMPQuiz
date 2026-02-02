package ygmd.kmpquiz.domain.viewModel.qandas.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.model.draftqanda.DraftQanda
import ygmd.kmpquiz.domain.model.qanda.Qanda
import ygmd.kmpquiz.domain.usecase.category.CategoryUseCase
import ygmd.kmpquiz.domain.usecase.qanda.DeleteQandasUseCase
import ygmd.kmpquiz.domain.usecase.qanda.GetQandaUseCase
import ygmd.kmpquiz.domain.usecase.qanda.SaveQandasUseCase
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableCategory
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableQanda
import ygmd.kmpquiz.domain.viewModel.error.UiError
import ygmd.kmpquiz.domain.viewModel.state.UiState

private val logger = Logger.withTag("SavedQandasViewModel")

class QandaOfCategoryViewModel(
    private val categoryId: String,
    private val getQandaUseCase: GetQandaUseCase,
    private val saveQandaUseCase: SaveQandasUseCase,
    private val deleteQandasUseCase: DeleteQandasUseCase,
    private val categoryUseCase: CategoryUseCase,
) : ViewModel() {
    val savedState: StateFlow<UiState<List<DisplayableQanda>>> = getQandaUseCase.observeSaved()
        .map { qandas ->
            qandas.filter { it.categoryId == categoryId }
                .map { toDisplayableQanda(it) }
                .sortedBy { it.contextKey }
        }
        .catch { UiState.Error(null, UiError.LoadQandaFailed) }
        .map { UiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    private fun toDisplayableQanda(qanda: Qanda): DisplayableQanda {
        val category = categoryUseCase.getById(qanda.categoryId)
        return DisplayableQanda(
            id = qanda.id,
            contextKey = qanda.contextKey,
            category = DisplayableCategory(category.id, category.name),
            question = qanda.question,
            answers = qanda.answers
        )
    }

    fun processPersistenceIntent(persistanceIntent: PersistanceIntent) {
        when (persistanceIntent) {
            is PersistanceIntent.DeleteQanda -> {
                deleteQanda(persistanceIntent.qandaId)
            }

            is PersistanceIntent.Save -> {
                save(persistanceIntent.qanda)
            }

            else -> logger.e { "Intent not handled: $persistanceIntent" }
        }
    }

    private fun saveAll(qandas: List<DraftQanda>) {
        viewModelScope.launch {
            qandas.forEach { saveQandaUseCase.save(it) }
        }
    }

    private fun save(qanda: DraftQanda) {
        viewModelScope.launch {
            saveQandaUseCase.save(qanda)
        }
    }

    private fun deleteQanda(qandaId: String) {
        viewModelScope.launch {
            deleteQandasUseCase.deleteById(qandaId)
        }
    }
}