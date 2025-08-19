package ygmd.kmpquiz.viewModel.save

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.application.usecase.qanda.DeleteQandasUseCase
import ygmd.kmpquiz.application.usecase.qanda.GetQandaUseCase
import ygmd.kmpquiz.domain.entities.qanda.Qanda

sealed interface PersistanceIntent {
    data class DeleteByIdentifier(val category: String) : PersistanceIntent
    data class DeleteQanda(val qandaId: String) : PersistanceIntent
}

data class SavedQandasUiState(
    val identifiedQandas: List<CategoryQandas> = emptyList()
)

data class CategoryQandas(
    val identifier: String,
    val qandas: List<Qanda>
)

class SavedQandasViewModel(
    getQandaUseCase: GetQandaUseCase,
    private val deleteQandasUseCase: DeleteQandasUseCase
) : ViewModel() {

    val savedState: StateFlow<SavedQandasUiState> = getQandaUseCase.observeAll()
        .map { qandas ->
            println(qandas)
            SavedQandasUiState(
                qandas.mapNotNull { qanda -> qanda.metadata.category?.let { it to qanda } }
                    .groupBy({ it.first }, { it.second })
                    .map { CategoryQandas(it.key, it.value) }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SavedQandasUiState()
        )

    fun processIntent(persistanceIntent: PersistanceIntent) {
        when (persistanceIntent) {
            is PersistanceIntent.DeleteByIdentifier ->
                viewModelScope.launch {
                    deleteQandasUseCase.deleteAllByCategory(persistanceIntent.category)
                }
            is PersistanceIntent.DeleteQanda ->
                viewModelScope.launch {
                    deleteQandasUseCase.deleteById(persistanceIntent.qandaId)
                }
        }
    }
}