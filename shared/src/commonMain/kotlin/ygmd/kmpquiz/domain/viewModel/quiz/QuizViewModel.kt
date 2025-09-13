package ygmd.kmpquiz.domain.viewModel.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.usecase.qanda.GetQandaUseCase
import ygmd.kmpquiz.domain.usecase.quiz.CreateQuizUseCase
import ygmd.kmpquiz.domain.usecase.quiz.DeleteQuizUseCase
import ygmd.kmpquiz.domain.usecase.quiz.GetQuizUseCase
import ygmd.kmpquiz.domain.entities.cron.QuizCron
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.entities.quiz.Quiz
import ygmd.kmpquiz.domain.viewModel.error.UiEvent

private val logger = Logger.withTag("QuizViewModel")

data class QuizzesUiState(
    val quizzes: Map<String, QuizState> = emptyMap(),
    val isLoading: Boolean = false,
    val isCreating: Boolean = false,
    val error: String? = null,
)

data class QuizState(
    val id: String,
    val title: String,
    val qandas: List<Qanda> = emptyList(),
)

sealed interface QuizIntent {
    data class CreateQuiz(
        val title: String,
        val qandas: List<Qanda>,
        val cronSetting: QuizCron?,
    ) : QuizIntent

    data class DeleteQuiz(val quizId: String) : QuizIntent
}

/**
 * TODO
 * - show snackbar at creation/error/deletion
 * - update back nav -> popstackback
 */

class QuizViewModel(
    private val getQuizUseCase: GetQuizUseCase,
    private val getQandaUseCase: GetQandaUseCase,
    private val createQuizUseCase: CreateQuizUseCase,
    private val deleteQuizUseCase: DeleteQuizUseCase,
) : ViewModel() {
    private var _quizzesState = MutableStateFlow(QuizzesUiState())
    val quizzesState = _quizzesState.asStateFlow()
    val qandas = getQandaUseCase.observeSaved()

    private val _quizEvents = MutableSharedFlow<UiEvent>(replay = 1)
    val quizEvents = _quizEvents.asSharedFlow()

    init {
        loadQuizzes()
    }

    private fun loadQuizzes() {
        viewModelScope.launch {
            getQuizUseCase
                .observeAll()
                .collect { quizzes ->
                    val quizzesById = quizzes
                        .map { it.toQuizUiState() }
                        .associateBy { it.id }

                    _quizzesState.value = QuizzesUiState(quizzesById)
                }
        }
    }

    fun processIntent(quizIntent: QuizIntent) {
        when (quizIntent) {
            is QuizIntent.CreateQuiz -> {
                return createQuiz(
                    title = quizIntent.title,
                    qandas = quizIntent.qandas,
                    cronSetting = quizIntent.cronSetting,
                )
            }

            is QuizIntent.DeleteQuiz -> deleteQuiz(quizIntent.quizId)
        }
    }

    private fun createQuiz(
        title: String,
        qandas: List<Qanda>,
        cronSetting: QuizCron?,
    ) {
        viewModelScope.launch {
            _quizzesState.value = _quizzesState.value.copy(isCreating = true, error = null)
            createQuizUseCase.createQuiz(
                title = title,
                qandas = qandas,
                cron = cronSetting
            ).fold(
                onSuccess = {
                    val quizState = QuizState(
                        id = it.id,
                        title = it.title,
                        qandas = it.qandas,
                    )

                    _quizzesState.update { currentState ->
                        val newMap = currentState.quizzes + (it.id to quizState)
                        currentState.copy(quizzes = newMap)
                    }

                    logger.i { "Created quiz: $title with id: ${it.id} and cron: ${cronSetting?.cron}" }
                    _quizEvents.tryEmit(
                        UiEvent.Success(
                            message = "Quiz created successfully",
                        )
                    )
                },
                onFailure = {
                    logger.e(it) { "Error creating quiz: $title" }
                    _quizEvents.tryEmit(
                        UiEvent.Success(
                            message = "Error creating quiz: ${it.message}",
                        )
                    )
                }
            )
        }
    }

    private fun deleteQuiz(quizId: String) {
        viewModelScope.launch {
            deleteQuizUseCase.deleteQuiz(quizId)
                .fold(
                    onSuccess = {
                        _quizzesState.update { currentState ->
                            currentState.copy(quizzes = currentState.quizzes - quizId)
                        }
                        logger.i { "Deleted quiz: $quizId" }
                    },
                    onFailure = {
                        logger.e(it) { "Error deleting quiz: $quizId" }
                    }
                )
        }
    }
}

private fun Quiz.toQuizUiState() = QuizState(
    id = id,
    title = title,
    qandas = qandas,
)