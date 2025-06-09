package ygmd.kmpquiz.viewModel.save

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.pojo.InternalQanda
import ygmd.kmpquiz.domain.repository.qanda.QandaRepository

class SavedQandasViewModel(
    private val qandaRepository: QandaRepository
) : ViewModel() {
    val savedState: StateFlow<SavedQandasUiState>
        get() = qandaRepository.getAll()
            .map {
                SavedQandasUiState.Success(
                    qandas = it,
                    categories = it.map { qanda -> qanda.category }.distinct()
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SavedQandasUiState.Loading
            )


    fun saveQanda(internalQanda: InternalQanda) {
        viewModelScope.launch {
            try {
                when (qandaRepository.save(internalQanda)) {
                    is Either.Left -> { }

                    is Either.Right -> { }
                }
            } catch (e: Exception) { }
        }
    }

    fun saveAll(qandas: List<InternalQanda>) {
        viewModelScope.launch {
            when (qandaRepository.saveAll(qandas)) {
                is Either.Right -> { }
                is Either.Left -> { }
            }
        }
    }

    fun deleteQanda(qanda: InternalQanda) {
        viewModelScope.launch {
            when (qandaRepository.deleteById(qanda.id!!)) {
                is Either.Right -> {}
                is Either.Left -> {}
            }
        }
    }

    fun toggleFavorite(qanda: InternalQanda) {
        viewModelScope.launch {
            // TODO handleToggleFavorite
        }
    }
}

// États UI inchangés
sealed class SavedQandasUiState {
    class Success(
        val qandas: List<InternalQanda>,
        val categories: List<String>
    ) : SavedQandasUiState() {
        fun containsContentKey(contentKey: String) =
            qandas.map { it.contentKey }.any { it == contentKey }
    }

    data object Loading : SavedQandasUiState()
}