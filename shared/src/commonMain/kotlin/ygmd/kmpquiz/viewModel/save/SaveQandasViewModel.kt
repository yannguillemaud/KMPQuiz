package ygmd.kmpquiz.viewModel.save

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.pojo.qanda.InternalQanda
import ygmd.kmpquiz.domain.usecase.DeleteQandasUseCase
import ygmd.kmpquiz.domain.usecase.GetQandasUseCase
import ygmd.kmpquiz.domain.usecase.SaveQandasUseCase

class SavedQandasViewModel(
    private val getQandasUseCase: GetQandasUseCase,
    private val saveQandaUseCase: SaveQandasUseCase,
    private val deleteQandasUseCase: DeleteQandasUseCase
) : ViewModel() {

    val savedState: StateFlow<SavedQandasUiState> = getQandasUseCase.execute()
        .map { qandas ->
            SavedQandasUiState.Success(
                qandas = qandas,
                categories = qandas.map { it.category }.distinct()
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SavedQandasUiState.Loading
        )

    fun saveQanda(qanda: InternalQanda) {
        viewModelScope.launch {
            saveQandaUseCase.save(qanda)
            // Pas besoin de mettre à jour manuellement !
            // Le Flow se met à jour automatiquement 🎉
        }
    }

    fun saveAll(qandas: List<InternalQanda>) {
        viewModelScope.launch {
            saveQandaUseCase.saveAll(qandas)
            // Idem, mise à jour automatique ! 🎉
        }
    }

    fun deleteQanda(qanda: InternalQanda) {
        viewModelScope.launch {
            deleteQandasUseCase.delete(qanda)
            // Idem, mise à jour automatique ! 🎉
        }
    }

    fun toggleFavorite(qanda: InternalQanda) {
        viewModelScope.launch {
            // TODO: Implémenter quand on aura les favoris
        }
    }
}

// États UI simplifiés
sealed class SavedQandasUiState {
    data object Loading : SavedQandasUiState()

    data class Success(
        val qandas: List<InternalQanda>,
        val categories: List<String>
    ) : SavedQandasUiState() {
        fun containsContentKey(contentKey: String) =
            qandas.any { it.contentKey == contentKey }
    }
}