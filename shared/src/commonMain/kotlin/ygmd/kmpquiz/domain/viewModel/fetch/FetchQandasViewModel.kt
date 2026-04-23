package ygmd.kmpquiz.domain.viewModel.fetch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.model.draftqanda.DraftQanda
import ygmd.kmpquiz.domain.result.FetchResult
import ygmd.kmpquiz.domain.usecase.fetch.FetchUseCase
import ygmd.kmpquiz.domain.usecase.qanda.SaveQandasUseCase
import ygmd.kmpquiz.events.event.Event

class FetchQandasViewModel(
    private val fetchQandaUseCase: FetchUseCase,
    private val saveQandaUseCase: SaveQandasUseCase,
) : ViewModel() {
    private val _events = Channel<Event>()
    private val _isDownloading = MutableStateFlow(false)
    val events = _events.receiveAsFlow()
    val isDownloading = _isDownloading.asStateFlow()

    fun fetch() {
        viewModelScope.launch {
            _isDownloading.value = true
            when (val result = fetchQandaUseCase()) {
                is FetchResult.Failure -> _events.send(
                    Event.SnackbarEvent("Failed to fetch qandas")
                )

                is FetchResult.Success<List<DraftQanda>> -> saveQandas(result.data)
            }
            _isDownloading.value = false
        }
    }

    private suspend fun saveQandas(qandas: List<DraftQanda>) {
        val result = saveQandaUseCase.saveAll(qandas)
        if (result.isFailure) {
            _events.send(Event.SnackbarEvent("Failed to save qandas"))
        }
    }
}