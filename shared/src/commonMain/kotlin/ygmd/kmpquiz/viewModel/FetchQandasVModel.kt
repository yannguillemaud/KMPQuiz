package ygmd.kmpquiz.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.pojo.InternalQanda
import ygmd.kmpquiz.domain.useCase.fetch.OpenTriviaFetchQanda

data class FetchQandasUiState(
    val isLoading: Boolean = false,
    val qandas: List<InternalQanda> = emptyList(),
    val error: String? = null,
)

class FetchQandasVModel(
    private val fetchQandasUseCase: OpenTriviaFetchQanda
): ViewModel() {
    private val _uiFetchedState = MutableStateFlow(FetchQandasUiState())
    val fetchedUiState = _uiFetchedState.asStateFlow()

    fun fetchQandas(){
        viewModelScope.launch {
            _uiFetchedState.value = FetchQandasUiState(isLoading = true, error = null)

            fetchQandasUseCase.fetch().fold(
                onSuccess = {
                    _uiFetchedState.value = _uiFetchedState.value.copy(
                        isLoading = false,
                        qandas = it,
                        error = null,
                    )
                },
                onFailure = {
                    println("Erreur: $it")
                    _uiFetchedState.value = _uiFetchedState.value.copy(
                        isLoading = false,
                        error = it.message ?: "Erreur inconnue",
                    )
                }
            )
        }
    }
}