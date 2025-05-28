package ygmd.kmpquiz.viewModel.fetch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.pojo.InternalQanda
import ygmd.kmpquiz.domain.repository.QandaRepository
import ygmd.kmpquiz.viewModel.fetch.DownloadedState.DOWNLOADED

data class SaveUiState(
    val savedQandasState: MutableMap<InternalQanda, DownloadedState> = mutableMapOf(),
)

sealed class DownloadedState {
    data object NOT_DOWNLOADED : DownloadedState()
    data object DOWNLOADING : DownloadedState()
    data object DOWNLOADED : DownloadedState()
    data object ERROR : DownloadedState()
}

class SaveQandasVModel(
    private val qandaRepository: QandaRepository,
) : ViewModel() {
    private val _saveUiState = MutableStateFlow(SaveUiState())
    val saveUiState = _saveUiState.asStateFlow()

    init {
        observeSavedQandas()
    }

    private fun observeSavedQandas() {
        viewModelScope.launch {
            qandaRepository.getAll()
                .collect { result ->
                    when (result) {
                        is Either.Left -> {
                            // TODO process error
                        }

                        is Either.Right -> {
                            _saveUiState.update { currentState ->
                                val newStates = result.value.associateWith { DOWNLOADED }
                                currentState.copy(savedQandasState = newStates.toMutableMap())
                            }
                        }
                    }
                }
        }
    }

    fun saveQanda(qanda: InternalQanda) {
        viewModelScope.launch {
            qandaRepository.save(qanda).fold(
                ifLeft = {
                    // TODO PROCESS ERROR
                },
                ifRight = { id ->
                    _saveUiState.update { current ->
                        val toAdd = qanda.copy(id = id)
                        current.savedQandasState[toAdd] = DOWNLOADED
                        current
                    }
                }
            )
        }
    }
}