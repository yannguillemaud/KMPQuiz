package ygmd.kmpquiz.domain.viewModel.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.usecase.cron.ToggleCronUseCase
import ygmd.kmpquiz.domain.usecase.notification.RescheduleTasksUseCase
import ygmd.kmpquiz.domain.usecase.quiz.DeleteQuizUseCase
import ygmd.kmpquiz.domain.usecase.quiz.GetQuizUseCase
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableQuiz
import ygmd.kmpquiz.domain.viewModel.displayable.displayable
import ygmd.kmpquiz.domain.viewModel.error.UiError
import ygmd.kmpquiz.domain.viewModel.error.UiEvent
import ygmd.kmpquiz.domain.viewModel.state.UiState


class QuizViewModel(
    private val getQuizUseCase: GetQuizUseCase,
    private val deleteQuizUseCase: DeleteQuizUseCase,
    private val toggleCronUseCase: ToggleCronUseCase,
    private val rescheduleTasksUseCase: RescheduleTasksUseCase,
) : ViewModel() {
    private val _quizEvents = MutableSharedFlow<UiEvent>(replay = 5)
    val quizEvents = _quizEvents.asSharedFlow()

    val quizzesState: StateFlow<UiState<List<DisplayableQuiz>>> = getQuizUseCase
        .observeAll()
        .map { it.map { quiz -> quiz.displayable() } }
        .map { UiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading,
        )


    fun processIntent(quizzesIntent: QuizzesIntent) {
        when (quizzesIntent) {
            is QuizzesIntent.DeleteQuiz -> {
                deleteQuiz(quizzesIntent.quizId)
                rescheduleAllQuizzes()
            }
            is QuizzesIntent.ToggleCron -> {
                toggleCron(quizzesIntent.quizId, quizzesIntent.isEnabled)
            }
        }
    }

    private fun rescheduleAllQuizzes(){
        viewModelScope.launch {
            rescheduleTasksUseCase.rescheduleAll()
        }
    }

    private fun rescheduleForQuiz(quizId: String){
        viewModelScope.launch {
            rescheduleTasksUseCase.rescheduleQuiz(quizId)
        }
    }

    private fun toggleCron(quizId: String, isEnabled: Boolean) {
        viewModelScope.launch {
            toggleCronUseCase.toggleCron(quizId, isEnabled)
                .fold(
                    onSuccess = {
                        val newValue = if(isEnabled) "enabled" else "disabled"
                        _quizEvents.emit(UiEvent.Success("Reminder for quiz is now $newValue"))
                        rescheduleForQuiz(quizId)
                    },
                    onFailure = {
                        _quizEvents.emit(UiEvent.Error(UiError.SaveFailed))
                    }
                )
        }
    }

    private fun deleteQuiz(quizId: String) {
        viewModelScope.launch {
            deleteQuizUseCase.deleteQuiz(quizId)
                .fold(
                    onSuccess = {
                        _quizEvents.emit(UiEvent.Success("Quiz deleted"))
                    },
                    onFailure = {
                        _quizEvents.emit(UiEvent.Error(UiError.SaveFailed))
                    }
                )
        }
    }
}