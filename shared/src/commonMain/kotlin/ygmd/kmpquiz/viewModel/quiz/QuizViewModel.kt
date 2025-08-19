package ygmd.kmpquiz.viewModel.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ygmd.kmpquiz.application.usecase.qanda.GetQandaUseCase
import ygmd.kmpquiz.application.usecase.quiz.CreateQuizUseCase
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.entities.quiz.Quiz
import ygmd.kmpquiz.domain.repository.QuizRepository
import ygmd.kmpquiz.viewModel.settings.UiCronSetting

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
    val createdAt: kotlin.time.Instant
)

sealed interface QuizIntent {
    data class CreateQuiz(
        val title: String,
        // TODO - replace with better versions of qandas
        val selectedCategory: String,
        val qandas: List<Qanda> = emptyList(),
        val cronSetting: UiCronSetting?,
    ) : QuizIntent

    data class DeleteQuiz(val quizId: String) : QuizIntent
}

class QuizViewModel(
    private val quizRepository: QuizRepository,
    getQandaUseCase: GetQandaUseCase,
    private val createQuizUseCase: CreateQuizUseCase,
) : ViewModel() {
    private var _quizzesState = MutableStateFlow(QuizzesUiState())
    val quizzesState = _quizzesState.asStateFlow()
    val qandas = getQandaUseCase.observeAll()

    init {
        loadQuizzes()
    }

    private fun loadQuizzes() {
        viewModelScope.launch {
            quizRepository.observeAll()
                .distinctUntilChanged()
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
                    selectedCategory = quizIntent.selectedCategory,
                    cronSetting = quizIntent.cronSetting,
                )
            }

            is QuizIntent.DeleteQuiz -> deleteQuiz(quizIntent.quizId)
        }
    }

    private fun createQuiz(
        title: String,
        selectedCategory: String,
        cronSetting: UiCronSetting?,
    ) {
        viewModelScope.launch {
            _quizzesState.value = _quizzesState.value.copy(isCreating = true, error = null)
            createQuizUseCase.createBy(
                title = title,
                predicate = { it.metadata.category == selectedCategory },
                QuizCron = cronSetting?.toCron()
            ).fold(
                onSuccess = {
                    val quizState = QuizState(
                        id = it.id,
                        title = it.title,
                        qandas = it.qandas,
                        createdAt = it.createdAt
                    )

                    _quizzesState.update { currentState ->
                        val newMap = currentState.quizzes + (it.id to quizState)
                        currentState.copy(quizzes = newMap)
                    }

                    logger.i { "Created quiz: $title with id: ${it.id} and cron: ${cronSetting?.cronExpression}" }
                },
                onFailure = {
                    logger.e(it) { "Error creating quiz: $title" }
                }
            )
        }
    }

    private fun deleteQuiz(quizId: String) {
        viewModelScope.launch {
            quizRepository.deleteQuizById(quizId)
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
    createdAt = createdAt
)