package ygmd.kmpquiz.domain.viewModel.quiz.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.model.qanda.Choice
import ygmd.kmpquiz.domain.model.quiz.QuizResult
import ygmd.kmpquiz.domain.model.quiz.QuizSession
import ygmd.kmpquiz.domain.usecase.category.CategoryUseCase
import ygmd.kmpquiz.domain.usecase.quiz.GetQuizUseCase
import ygmd.kmpquiz.domain.usecase.quizSession.QuizSessionUseCase
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableCategory
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableQanda
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableQuizSession
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableQuizSession.Completed
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableQuizSession.InProgress
import ygmd.kmpquiz.domain.viewModel.error.UiError
import ygmd.kmpquiz.domain.viewModel.state.UiState

sealed interface QuizSessionIntent {
    data class SelectAnswer(val answer: Choice) : QuizSessionIntent
    data object NextState : QuizSessionIntent
}

private val logger = Logger.withTag("QuizViewModel")

class QuizSessionViewModel(
    val quizId: String,
    private val getQuizUseCase: GetQuizUseCase,
    private val quizSessionUseCase: QuizSessionUseCase,
    private val categoryUseCase: CategoryUseCase,
) : ViewModel() {
    init {
        initQuizSession(quizId)
    }

    val uiState: StateFlow<UiState<DisplayableQuizSession>> = quizSessionUseCase.observeSession()
        .combine(categoryUseCase.observeCategories()){ session, categories ->
            session to categories.associateBy { it.id }
        }
        .map { (session, categoriesById) ->
            if (session == null) UiState.Loading
            else UiState.Success(
                data =
                    if (session.isCompleted) Completed(session, results = computeResults(session))
                    else {
                        val currentQanda = session.currentQanda
                        val answers = session.currentShuffledAnswers
                        if (currentQanda == null || answers == null) {
                            return@map UiState.Error(
                                null,
                                UiError.LoadQandaFailed
                            )
                        } else {
                            val category =
                                categoriesById[currentQanda.categoryId] ?: return@map UiState.Error(
                                    null,
                                    UiError.LoadCategoryFailed
                                )
                            InProgress(
                                session = session,
                                currentQanda = DisplayableQanda(
                                    id = currentQanda.id,
                                    contextKey = currentQanda.contextKey,
                                    question = currentQanda.question,
                                    answers = answers,
                                    category = DisplayableCategory(category.id, category.name)
                                ),
                                selectedAnswer = session.selectedAnswer,
                                shuffledAnswers = answers
                            )
                        }
                    }
            )
        }
        .catch { UiState.Error(null, UiError.LoadQuizFailed) }
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5000),
            UiState.Loading
        )

    private fun initQuizSession(quizId: String) {
        viewModelScope.launch {
            getQuizUseCase.getQuizById(quizId)
                .fold(
                    onSuccess = {
                        quizSessionUseCase.initSession(it)
                    },
                    onFailure = {
                        logger.e(it) { "Failed to load quiz" }
                    }
                )
        }
    }

    fun processIntent(intent: QuizSessionIntent) {
        when (intent) {
            QuizSessionIntent.NextState -> processNextState()
            is QuizSessionIntent.SelectAnswer -> processSelectedAnswer(intent.answer)
        }
    }

    private fun processNextState() {
        viewModelScope.launch {
            quizSessionUseCase.nextState()
        }
    }

    private fun processSelectedAnswer(answer: Choice) {
        viewModelScope.launch {
            quizSessionUseCase.selectAnswer(answer)
        }
    }

    private fun computeResults(session: QuizSession): QuizResult {
        val score = session.userAnswers.count { (index, choice) ->
            session.quiz.qandas[index].correctAnswer == choice
        }

        return QuizResult(session.userAnswers.size, score)
    }
}
