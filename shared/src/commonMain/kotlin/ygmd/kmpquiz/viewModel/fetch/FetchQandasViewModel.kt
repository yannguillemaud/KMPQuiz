package ygmd.kmpquiz.viewModel.fetch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ygmd.kmpquiz.application.usecase.qanda.FetchQandasUseCase
import ygmd.kmpquiz.application.usecase.qanda.GetQandasUseCase
import ygmd.kmpquiz.data.repository.service.FetchResult
import ygmd.kmpquiz.domain.error.toViewModelError

class FetchQandasViewModel(
    private val fetchQandaUseCase: FetchQandasUseCase,
    private val getQandasUseCase: GetQandasUseCase,
) : ViewModel() {

    private val _fetchState = MutableStateFlow<FetchState>(FetchState.Idle)

    val fetchState = combine(
        _fetchState,
        getQandasUseCase.observeAll()
    ) { fetched, observed ->
        FetchState.Success(observed)
    }

//    val fetchState: StateFlow<FetchState> = combine(
//        _fetchState,
//        getQandasUseCase.observeAll(),
//    ) { apiState, savedQandas ->
//        when (apiState) {
//            is FetchApiState.Idle -> FetchState.Idle
//            is FetchApiState.Loading -> FetchState.Loading
//            is FetchApiState.Error -> FetchState.Error(apiState.error)
//            is FetchApiState.Success -> {
//                if (apiState.data.isEmpty()) FetchState.Idle
//                val qandasWithState = apiState.data.map { fetched ->
//                    val isAlradySaved = savedQandas.any { saved ->
//                        saved.contextKey == fetched.contextKey
//                    }
//                    QandaUiState(
//                        qanda = fetched,
//                        downloadState = if (isAlradySaved) Downloaded else NotDownloaded
//                    )
//                }
//                FetchState.Success(qandasWithState)
//            }
//        }
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5_000),
//        initialValue = FetchState.Idle
//    )

    init {
        fetchQandas()
    }

    fun fetchQandas() {
        viewModelScope.launch {
            _fetchState.value = FetchState.Loading
            when (val result = fetchQandaUseCase.fetch()) {
                is FetchResult.Success -> {
                    _fetchState.value = FetchState.Success(result.data)
                }

                else -> _fetchState.value = FetchState.Error(result.toViewModelError())
            }
        }
    }
}