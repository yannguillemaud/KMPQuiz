package ygmd.kmpquiz.domain.viewModel.fetch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.model.draftqanda.DraftQanda
import ygmd.kmpquiz.domain.result.FetchResult
import ygmd.kmpquiz.domain.usecase.category.CategoryUseCase
import ygmd.kmpquiz.domain.usecase.fetch.FetchUseCase
import ygmd.kmpquiz.domain.usecase.qanda.GetQandaUseCase
import ygmd.kmpquiz.domain.usecase.qanda.SaveQandaUseCase
import ygmd.kmpquiz.domain.usecase.quiz.GetQuizUseCase
import ygmd.kmpquiz.events.event.Event

data class HomeContentState(
    val totalCategoriesCount: Int = 0,
    val totalQandasCount: Int = 0,
    val totalQuizCount: Int = 0,
    val isDownloading: Boolean = false
)

class HomeViewModel(
    private val fetchQandaUseCase: FetchUseCase,
    private val saveQandaUseCase: SaveQandaUseCase,
    private val categoryUseCase: CategoryUseCase,
    private val qandaUseCase: GetQandaUseCase,
    private val quizUseCase: GetQuizUseCase,
) : ViewModel() {
    private val _events = Channel<Event>()
    val events = _events.receiveAsFlow()

    private val _isDownloading = MutableStateFlow(false)
    val contentFlow = combine(
        categoryUseCase.observeCategories(),
        quizUseCase.observeQuizzes(),
        qandaUseCase.observeAll(),
        _isDownloading
    ) { categories, quizzes, qandas, isDownloading ->
        HomeContentState(
            totalCategoriesCount = categories.size,
            totalQuizCount = quizzes.size,
            totalQandasCount = qandas.size,
            isDownloading = isDownloading
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HomeContentState()
    )

    // TODO - refacto
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

    // TODO - refacto
    private suspend fun saveQandas(qandas: List<DraftQanda>) {
        var isSuccess = true
        for (qanda in qandas) {
            saveQandaUseCase(qanda).onFailure { isSuccess = false }
        }
        if (!isSuccess) {
            _events.send(Event.SnackbarEvent("Some qandas have not been saved"))
        }
    }
}