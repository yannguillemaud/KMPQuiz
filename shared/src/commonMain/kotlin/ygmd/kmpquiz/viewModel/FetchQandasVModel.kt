package ygmd.kmpquiz.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.pojo.QANDA
import ygmd.kmpquiz.domain.useCase.fetch.FetchQandasUseCase

data class FetchQandasUiState(
    val isLoading: Boolean = false,
    val qandas: List<QANDA> = emptyList(),
    val error: String? = null,
)

class FetchQandasVModel(
    private val fetchQandasUseCase: FetchQandasUseCase
): ViewModel() {
    private val _uiFetchedState = MutableStateFlow(FetchQandasUiState())
    val fetchedUiState = _uiFetchedState.asStateFlow()

    private val _uiSavedState = MutableStateFlow(SavedQandasUiState())
    val savedUiState = _uiSavedState.asStateFlow()

    fun fetchQandas(){
        viewModelScope.launch {
            _uiFetchedState.value = FetchQandasUiState(isLoading = true)

            try {
                fetchQandasUseCase().let {
                    _uiFetchedState.value = FetchQandasUiState(qandas = it)
                }
            } catch (e: Exception){
                _uiFetchedState.value = FetchQandasUiState(error = e.message)
            }
        }
    }
}