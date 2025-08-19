package ygmd.kmpquiz.viewModel.fetch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ygmd.kmpquiz.application.usecase.fetch.FetchQandasUseCase
import ygmd.kmpquiz.application.usecase.fetch.GetFetchQandasUseCase
import ygmd.kmpquiz.application.usecase.fetch.SaveFetchQandasUseCase
import ygmd.kmpquiz.application.usecase.qanda.GetQandaUseCase
import ygmd.kmpquiz.application.usecase.qanda.SaveQandasUseCase
import ygmd.kmpquiz.application.usecase.quiz.CreateQuizUseCase
import ygmd.kmpquiz.domain.repository.DraftQanda
import ygmd.kmpquiz.viewModel.error.ViewModelError
import ygmd.kmpquiz.viewModel.error.toViewModelError

private val logger = Logger.withTag(FetchQandasViewModel::class.java.name)
const val FETCH_DELAI_DURATION = 5_000L

data class FetchUiState(
    val qandasByCategory: Map<String, QandasByIdentifierState> = emptyMap(),
    val error: ViewModelError? = null,
    val isLoading: Boolean = false,
)

data class QandasByIdentifierState(
    val identifier: String,
    val qandas: List<DraftQanda>,
)

sealed interface FetchIntentAction {
    data object OnRefresh : FetchIntentAction
    data class OnSave(val groupedQandasState: QandasByIdentifierState) : FetchIntentAction
}

class FetchQandasViewModel(
    private val fetchQandaUseCase: FetchQandasUseCase,
    getFetchQandasUseCase: GetFetchQandasUseCase,
    private val saveFetchQandasUseCase: SaveFetchQandasUseCase,
    private val saveQandasUseCase: SaveQandasUseCase,
    getSavedQandasUseCase: GetQandaUseCase,
    private val createQuizUseCase: CreateQuizUseCase,
) : ViewModel() {
    private val _fetchState = MutableStateFlow(FetchUiState())
    private val _fetchedQandasFlow = getFetchQandasUseCase.observeFetched()
    private val _savedQandasFlow =
        getSavedQandasUseCase.observeAll()

    // TIMER
    private var fetchJob: Job? = null
    private val _canExecuteFetch = MutableStateFlow(true)
    val canExecuteFetch: StateFlow<Boolean> = _canExecuteFetch.asStateFlow()

    init {
        fetchQandas()
    }

    val fetchState: StateFlow<FetchUiState> = combine(
        _fetchState,
        _fetchedQandasFlow,
        _savedQandasFlow
    ) { currentState, fetchedQandas, savedQandas ->
        // Créer un Set des contextKeys des QandAs sauvegardées pour une recherche rapide
        val savedContextKeys = savedQandas.map { it.contextKey }.toSet()

        // Filtrer les QandAs fetchées pour exclure celles déjà sauvegardées
        val filteredFetchedQandas = fetchedQandas.filter { fetchedQanda ->
            fetchedQanda.contextKey !in savedContextKeys
        }

        // Grouper les QandAs filtrées
        val merged = currentState.qandasByCategory + filteredFetchedQandas.grouped()

        if (merged == currentState.qandasByCategory) currentState
        else currentState.copy(qandasByCategory = merged)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        FetchUiState()
    )

    fun fetchQandas() {
        if (!_canExecuteFetch.value) {
            logger.w { "Action bloquée par le délai" }
            return
        }

        viewModelScope.launch {
            _fetchState.value = _fetchState.value.copy(isLoading = true, error = null)
            fetchQandaUseCase.fetch().fold(
                onFailure = {
                    _fetchState.value = _fetchState.value.copy(
                        isLoading = false,
                        error = it.toViewModelError()
                    )
                },
                onSuccess = { fetchedQandas ->
                    saveFetchQandasUseCase.saveFetched(fetchedQandas)
                    _fetchState.value = _fetchState.value.copy(isLoading = false)
                }
            )
            startFetchDelaiTimer()
        }
    }

    private fun startFetchDelaiTimer() {
        fetchJob?.cancel()
        _canExecuteFetch.value = false
        fetchJob = viewModelScope.launch {
            delay(FETCH_DELAI_DURATION)
            _canExecuteFetch.value = true
            logger.d { "Delai expiré, action disponible" }
        }
    }

    fun onIntentAction(fetchIntentAction: FetchIntentAction) {
        when (fetchIntentAction) {
            is FetchIntentAction.OnSave -> {
                saveQandas(fetchIntentAction.groupedQandasState)
            }

            is FetchIntentAction.OnRefresh -> {
                fetchQandas()
            }
        }
    }

    private fun saveQandas(groupedQandasState: QandasByIdentifierState) {
        viewModelScope.launch {
            _fetchState.update { it.copy(isLoading = true) }

            saveQandasUseCase.saveAll(groupedQandasState.qandas)
                .fold(
                    onFailure = { error ->
                        _fetchState.update {
                            it.copy(
                                isLoading = false,
                                error = error.toViewModelError()
                            )
                        }
                    },
                    onSuccess = {
                        val category = groupedQandasState.identifier
                        createQuizUseCase.updateBy(category, { it.metadata.category == category })
                        saveFetchQandasUseCase.removeFetched(groupedQandasState.qandas)
                        _fetchState.update { it.copy(isLoading = false) }
                    }
                )
        }
    }

    override fun onCleared() {
        super.onCleared()
        fetchJob?.cancel()
    }
}

private fun List<DraftQanda>.grouped(): Map<String, QandasByIdentifierState> =
    groupBy({ it.category }, { it })
        .mapValues {
            QandasByIdentifierState(
                identifier = it.key,
                qandas = it.value
            )
        }