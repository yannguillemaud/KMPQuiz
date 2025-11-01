package ygmd.kmpquiz.domain.viewModel.qandas.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.model.qanda.QandaEdit
import ygmd.kmpquiz.domain.usecase.category.CategoryUseCase
import ygmd.kmpquiz.domain.usecase.qanda.QandaEditUseCase
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableCategory
import ygmd.kmpquiz.domain.viewModel.error.UiError
import ygmd.kmpquiz.domain.viewModel.error.UiError.SaveQandaFailed
import ygmd.kmpquiz.domain.viewModel.error.UiEvent
import ygmd.kmpquiz.domain.viewModel.error.UiEvent.Error
import ygmd.kmpquiz.domain.viewModel.error.UiEvent.Success
import ygmd.kmpquiz.domain.viewModel.state.UiState

sealed interface QandaEditIntent {
    data class UpdateQuestion(val question: String) : QandaEditIntent
    data class UpdateIncorrectAnswers(val index: Int, val newValue: String) : QandaEditIntent
    data class UpdateCorrectAnswer(val newValue: String) : QandaEditIntent
    data class UpdateCategory(val newValue: String) : QandaEditIntent
    data class RemoveAnswer(val indexToRemove: Int) : QandaEditIntent
    data object AddNewIncorrectAnswer : QandaEditIntent
    data object Save : QandaEditIntent
}

private val logger = Logger.withTag("QandaEditViewModel")

class QandaEditViewModel(
    qandaId: String?,
    private val qandaEditUseCase: QandaEditUseCase,
    private val categoryUseCase: CategoryUseCase,
) : ViewModel() {
    val qandaEditState: StateFlow<UiState<QandaEditUiState>> = qandaEditUseCase.observeQandaEdit()
        .catch { e -> logger.e(e) { "Error loading qanda" } }
        .map { qandaEdit ->
            mapToUiState(qandaEdit, qandaId)
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            UiState.Loading
        )

    private val _qandaEditEvents: MutableSharedFlow<UiEvent> = MutableSharedFlow(replay = 1)
    val qandaEditEvents: SharedFlow<UiEvent> = _qandaEditEvents.asSharedFlow()

    private fun mapToUiState(
        qandaEdit: QandaEdit?,
        qandaId: String?
    ): UiState<QandaEditUiState> = when (qandaEdit) {
        null if qandaId != null -> UiState.Error(null, UiError.LoadQandaFailed)
        null -> UiState.Success(
            QandaEditUiState(
                question = TextField("", "Question"),
                category = null,
                correctAnswer = TextField("", "Correct answer"),
            )
        )

        else -> {
            val category = qandaEdit.categoryId?.let { categoryUseCase.getById(it) } ?: return UiState.Error<QandaEditUiState>(null, UiError.LoadQandaFailed)
            UiState.Success(
                QandaEditUiState(
                    question = TextField(qandaEdit.question, "Question"),
                    category = DisplayableCategory(category.id, category.name),
                    correctAnswer = TextField(qandaEdit.correctAnswer, "Correct answer"),
                    incorrectAnswers = qandaEdit.incorrectAnswers.entries
                        .associateBy({ it.key }, { TextField(it.value, "Incorrect answer") }),
                    canAddIncorrectAnswer = qandaEdit.canAddNewIncorrectAnwer,
                )
            )
        }
    }

    init {
        qandaId?.let {
            loadQandaEdit(it)
        }
    }

    private fun loadQandaEdit(qandaId: String) {
        viewModelScope.launch {
            qandaEditUseCase.load(qandaId)
        }
    }

    fun processIntent(intent: QandaEditIntent) {
        when (intent) {
            is QandaEditIntent.UpdateQuestion -> {
                qandaEditUseCase.updateQuestion(intent.question)
            }

            is QandaEditIntent.Save -> {
                viewModelScope.launch {
                    qandaEditUseCase.save()
                        .fold(
                            onSuccess = { _qandaEditEvents.emit(Success("Qanda saved")) },
                            onFailure = {
                                _qandaEditEvents.emit(
                                    Error(
                                        SaveQandaFailed(
                                            it.message ?: "Save failed"
                                        )
                                    )
                                )
                            }
                        )
                }
            }

            is QandaEditIntent.AddNewIncorrectAnswer -> {
                qandaEditUseCase.addNewIncorrectAnswer()
            }

            is QandaEditIntent.RemoveAnswer -> {
                qandaEditUseCase.removeAnswer(intent.indexToRemove)
            }

            is QandaEditIntent.UpdateCorrectAnswer -> qandaEditUseCase.updateCorrectAnswer(intent.newValue)
            is QandaEditIntent.UpdateIncorrectAnswers -> qandaEditUseCase.updateAnswers(
                intent.index,
                intent.newValue
            )

            is QandaEditIntent.UpdateCategory -> qandaEditUseCase.updateCategory(intent.newValue)
        }
    }

    override fun onCleared() {
        qandaEditUseCase.reset()
    }
}