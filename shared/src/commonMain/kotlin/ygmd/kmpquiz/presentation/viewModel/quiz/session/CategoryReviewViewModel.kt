package ygmd.kmpquiz.presentation.viewModel.quiz.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.core.domain.session.Session
import ygmd.kmpquiz.core.usecase.session.ObserveSessionUseCase
import ygmd.kmpquiz.core.usecase.session.SessionQuestionReviewUseCase
import ygmd.kmpquiz.core.usecase.session.SessionResultsUseCase

sealed interface CategoryReviewUiState {
    data object Loading : CategoryReviewUiState
    data class Error(val message: String) : CategoryReviewUiState
    data class Loaded(
        val categoryName: String,
        val userAnswers: List<Pair<QandaUiState, UserAnswerState>>,
    ) : CategoryReviewUiState
}

/**
 * Backs the per-category review screen: observes a completed session by id, computes its
 * [SessionResults][ygmd.kmpquiz.core.domain.session.SessionResults], filters the per-question
 * review to a single category, and exposes it as render-ready `(QandaUiState, UserAnswerState)`
 * pairs. Mirrors [DetailedSessionViewModel]'s observe→results→review chain, minus the quiz
 * title (the category name arrives directly via [initialize]).
 */
class CategoryReviewViewModel(
    private val observeSessionUseCase: ObserveSessionUseCase,
    private val sessionResultsUseCase: SessionResultsUseCase,
    private val sessionQuestionReviewUseCase: SessionQuestionReviewUseCase,
) : ViewModel() {
    private val _session = MutableStateFlow<Session?>(null)
    private val _categoryId = MutableStateFlow<String?>(null)
    private val _categoryName = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val state = combine(_session, _categoryId, _categoryName) { session, categoryId, categoryName ->
        Triple(session, categoryId, categoryName)
    }.flatMapLatest { (session, categoryId, categoryName) ->
        when {
            session == null || categoryId == null -> flowOf(CategoryReviewUiState.Loading)
            session.state !is Session.SessionState.Completed -> flowOf(CategoryReviewUiState.Loading)
            else -> flow<CategoryReviewUiState> {
                val results = sessionResultsUseCase(session)
                val userAnswers = sessionQuestionReviewUseCase(results, categoryId)
                    .toUserAnswerUiStates()
                emit(CategoryReviewUiState.Loaded(categoryName, userAnswers))
            }
        }.catch { emit(CategoryReviewUiState.Error(it.message ?: "Unknown error")) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CategoryReviewUiState.Loading,
    )

    fun initialize(sessionId: String, categoryId: String, categoryName: String) {
        if (_session.value != null) return
        _categoryId.value = categoryId
        _categoryName.value = categoryName
        viewModelScope.launch {
            observeSessionUseCase.observeSession(sessionId).collect { session ->
                if (session == null) return@collect
                _session.value = session
            }
        }
    }
}
