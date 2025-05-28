package ygmd.kmpquiz.viewModel.fetch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import ygmd.kmpquiz.domain.pojo.InternalQanda
import ygmd.kmpquiz.domain.pojo.contentKey
import ygmd.kmpquiz.domain.repository.OperationError
import ygmd.kmpquiz.domain.repository.OperationError.*
import ygmd.kmpquiz.domain.repository.QandaRepository
import ygmd.kmpquiz.domain.useCase.fetch.OpenTriviaFetchQanda
import ygmd.kmpquiz.viewModel.fetch.DownloadedState.*
import ygmd.kmpquiz.viewModel.fetch.FetchError.ApiResponseError
import ygmd.kmpquiz.viewModel.fetch.FetchError.RetryExceeded

val logger = Logger.withTag("${FetchQandasVModel::class.simpleName}")

class FetchQandasVModel(
    private val fetchQandasUseCase: OpenTriviaFetchQanda,
    private val qandaRepository: QandaRepository,
) : ViewModel() {
    private val _fetchedState = MutableStateFlow(FetchQandasUiState())
    val fetchedUiState = _fetchedState.asStateFlow()

    init {
        fetchQandas()
        observeSaveAsState()
    }

    private fun fetchQandas() {
        viewModelScope.launch {
            _fetchedState.value = FetchQandasUiState(isLoading = true, error = null)

            fetchQandasUseCase.fetch().fold(
                onSuccess = {
                    _fetchedState.value = _fetchedState.value.copy(
                        isLoading = false,
                        fetchQandas = processFetchedQandas(it),
                        error = null,
                    )
                },
                onFailure = { error ->
                    _fetchedState.value = _fetchedState.value.copy(
                        isLoading = false,
                        error = when (error) {
                            is SerializationException,
                            is IllegalArgumentException -> RetryExceeded

                            else -> ApiResponseError
                        },
                    )
                }
            )
        }
    }

    private suspend fun processFetchedQandas(fetchedQandas: List<InternalQanda>): Map<InternalQanda, DownloadedState> {
        return fetchedQandas.associateWith { qanda ->
            qandaRepository.existsByContentKey(qanda).fold(
                ifLeft = {
                    when(it){
                        is NotFound -> NOT_DOWNLOADED
                        is Error -> ERROR
                        is AlreadyExists -> DOWNLOADED
                    }
                },
                ifRight = { DOWNLOADED }
            )
        }
    }

    private fun observeSaveAsState() {
        viewModelScope.launch {
            qandaRepository.getAll().collect { saveState ->
                saveState.onRight { savedQandas ->
                    _fetchedState.update { currentFetchState ->
                        val updatedQuandas = currentFetchState.fetchQandas.toMutableMap()
                        savedQandas.forEach { savedQanda ->
                            val matchingKey = updatedQuandas.keys.find {
                                it.contentKey() == savedQanda.contentKey()
                            }
                            matchingKey?.let {
                                updatedQuandas[it] = DOWNLOADED
                            }
                        }

                        currentFetchState.copy(fetchQandas = updatedQuandas)
                    }
                }
            }
        }
    }
}