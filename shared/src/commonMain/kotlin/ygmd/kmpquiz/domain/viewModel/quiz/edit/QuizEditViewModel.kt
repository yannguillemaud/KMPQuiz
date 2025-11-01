package ygmd.kmpquiz.domain.viewModel.quiz.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.model.cron.CronExpression
import ygmd.kmpquiz.domain.model.cron.QuizCronPreset
import ygmd.kmpquiz.domain.usecase.category.CategoryUseCase
import ygmd.kmpquiz.domain.usecase.notification.RescheduleTasksUseCase
import ygmd.kmpquiz.domain.usecase.quiz.QuizEditUseCase
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableCategory
import ygmd.kmpquiz.domain.viewModel.error.UiError
import ygmd.kmpquiz.domain.viewModel.error.UiEvent
import ygmd.kmpquiz.domain.viewModel.state.UiState

private val logger = Logger.withTag("QuizEditViewModel")

class QuizEditViewModel(
    private val quizEditUseCase: QuizEditUseCase,
    categoryUseCase: CategoryUseCase,
    private val rescheduleTasksUseCase: RescheduleTasksUseCase,
) : ViewModel() {
    val _quizEditUiState = quizEditUseCase.observeQuizEdit()

    val quizEditUiState: StateFlow<UiState<QuizEditUiState>> = _quizEditUiState
        .catch { logger.e(it) { "Error loading quiz" } }
        .combine(categoryUseCase.observeCategories()) { quiz, categories ->
            val categoriesById = categories.associateBy { it.id }
            if (quiz == null) {
                UiState.Error(null, UiError.LoadQuizFailed)
            } else {
                val categories = quiz.categories.map {
                    categoriesById[it] ?: return@combine UiState.Error(
                        null,
                        UiError.LoadCategoryFailed
                    )
                }
                UiState.Success(
                    QuizEditUiState(
                        title = quiz.title,
                        titleError = when {
                            quiz.title.isEmpty() -> "Title cannot be empty"
                            quiz.title.length > 15 -> "Quiz cannot be longer than 15 characters"
                            else -> null
                        },
                        categories = categories
                            .map { DisplayableCategory(it.id, it.name) },
                        cron = quiz.cron?.cron,
                        cronEnabled = quiz.cron?.isEnabled == true
                    )
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    private val _availableCrons = MutableStateFlow(emptyList<CronExpression>())
    val availableCrons = _availableCrons.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>(replay = 1)
    val events = _events.asSharedFlow()

    init {
        loadCrons()
    }

    private fun loadCrons() {
        viewModelScope.launch {
            _availableCrons.value = QuizCronPreset.entries
                .map { it.toCronExpression() }
        }
    }

    private fun loadQuiz(quizId: String) {
        viewModelScope.launch {
            quizEditUseCase.load(quizId)
        }
    }

    fun processIntent(intent: QuizEditIntent) {
        when (intent) {
            is QuizEditIntent.Save -> {
                trySave()
            }
            is QuizEditIntent.UpdateTitle -> {
                updateTitle(intent.title)
            }
            is QuizEditIntent.UpdateCategories -> {
                updateCategories(intent.categories)
            }
            is QuizEditIntent.UpdateCron -> {
                updateCron(intent.cron)
            }
            is QuizEditIntent.Load -> {
                loadQuiz(intent.quizId)
            }
        }
    }

    private fun trySave(){
        viewModelScope.launch {
            val currentState = _quizEditUiState.first()
            if(currentState == null) {
                _events.emit(UiEvent.Error(UiError.SaveFailed))
                return@launch
            } else quizEditUseCase.trySave().fold(
                onFailure = {
                    logger.e(it) { "Error saving quiz" }
                    _events.emit(UiEvent.Error(UiError.SaveFailed))
                },
                onSuccess = {
                    _events.emit(UiEvent.Success("Quiz saved"))
                    rescheduleTasksUseCase.rescheduleQuiz(it)
                }
            )
        }
    }

    private fun updateCron(cron: CronExpression?) {
        quizEditUseCase.updateCron(cron)
    }

    private fun updateCategories(categories: List<DisplayableCategory>) {
        quizEditUseCase.updateCategories(categories.map { it.id })
    }

    private fun updateTitle(title: String) {
        quizEditUseCase.updateTitle(title)
    }

    override fun onCleared() {
        viewModelScope.launch {
            quizEditUseCase.reset()
        }
    }
}