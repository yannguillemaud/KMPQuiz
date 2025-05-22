package ygmd.kmpquiz.viewModel

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.pojo.QANDA
import ygmd.kmpquiz.domain.useCase.GetSavedQandaUseCase
import ygmd.kmpquiz.domain.useCase.SaveQandaUseCase

data class SavedQandasUiState(
    val isLoading: Boolean = false,
    val qandas: List<QANDA> = emptyList(),
    val error: String? = null,
)

class SaveQandasVModel (
    private val saveQandaUseCase: SaveQandaUseCase,
    private val getSavedQandaUseCase: GetSavedQandaUseCase,
): ViewModel(){
    private val _savedState = MutableStateFlow(SavedQandasUiState())
    val savedState = _savedState.asStateFlow()

    init {
        observe()
    }

    private fun observe(){
        viewModelScope.launch {
            getSavedQandaUseCase()
                .onStart {  _savedState.value = _savedState.value.copy(isLoading = true) }
                .catch { _savedState.value = SavedQandasUiState(error = it.message) }
                .collect {
                    _savedState.value = SavedQandasUiState(qandas = it, isLoading = false)
                }
        }
    }

    fun saveQandas(qanda: QANDA){
        viewModelScope.launch(Dispatchers.Main){
            try {
                if(getSavedQandaUseCase.exists(qanda).not()) saveQandaUseCase.saveQanda(qanda)
            } catch (e: Exception){
                _savedState.value = _savedState.value.copy(error = e.message)
            }
        }
    }
}