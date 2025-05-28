package ygmd.kmpquiz.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.pojo.InternalQanda

data class GetQandasUiState(
    val isLoading: Boolean = false,
    val qandas: List<InternalQanda> = emptyList(),
    val error: String? = null,
)

class GetQandasVModel(
//    private val getQandaUseCase: GetQandaUseCase,
): ViewModel(){
    private val _getQandasState = MutableStateFlow(GetQandasUiState())
    val qandasStateFlow = _getQandasState.asStateFlow()

    init {
        observe()
    }

    private fun observe(){
        viewModelScope.launch {
//            getQandaUseCase.getAll()
//                .onStart {  _getQandasState.value = _getQandasState.value.copy(isLoading = true) }
//                .catch { _getQandasState.value = GetQandasUiState(error = it.message) }
//                .collect {
//                    _getQandasState.value = GetQandasUiState(qandas = it, isLoading = false)
//                }
        }
    }
}