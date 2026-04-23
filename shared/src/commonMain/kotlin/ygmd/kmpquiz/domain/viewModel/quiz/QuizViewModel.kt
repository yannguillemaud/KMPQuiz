package ygmd.kmpquiz.domain.viewModel.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.usecase.cron.CronUseCase
import ygmd.kmpquiz.domain.usecase.notification.ScheduleAllQuizzesUseCase
import ygmd.kmpquiz.domain.usecase.quiz.DeleteQuizUseCase
import ygmd.kmpquiz.domain.usecase.quiz.GetQuizUseCase
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableCategory
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableQuiz
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableQuizCron
import ygmd.kmpquiz.domain.viewModel.error.UiError
import ygmd.kmpquiz.events.event.Event

data class QuizzesUiState(
    val isLoading: Boolean = false,
    val quizzes: List<DisplayableQuiz> = emptyList(),
    val error: UiError? = null,
)

class QuizViewModel(
    private val getQuizUseCase: GetQuizUseCase,
    private val deleteQuizUseCase: DeleteQuizUseCase,
    private val cronUseCase: CronUseCase,
    private val scheduleAllQuizzesUseCase: ScheduleAllQuizzesUseCase,
) : ViewModel() {
    private val _quizEvents = Channel<Event>()
    val quizEvents = _quizEvents.receiveAsFlow()

    val quizzesState: StateFlow<QuizzesUiState> = getQuizUseCase.observeAll()
        .map { quizzes ->
            val quizzes = quizzes.map { quiz ->
                DisplayableQuiz(
                    id = quiz.id,
                    title = quiz.title,
                    questionsSize = quiz.questionsCount,
                    categories = quiz.categories.map {
                        DisplayableCategory(it.id, it.name)
                    },
                    cron = quiz.cron?.let {
                        DisplayableQuizCron(
                            it.id,
                            it.name,
                            it.expression,
                            it.isEnabled
                        )
                    },
                )
            }
            QuizzesUiState(isLoading = false, quizzes = quizzes)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = QuizzesUiState(isLoading = true),
        )

    fun toggleCron(quizId: String, cronId: String, isEnabled: Boolean) {
        viewModelScope.launch {
            cronUseCase.toggleCron(quizId, cronId = cronId, newValue = isEnabled)
                .onFailure {
                    _quizEvents.send(Event.SnackbarEvent("Failed to update quiz cron"))
                }
        }
    }

    fun deleteQuiz(quizId: String) {
        viewModelScope.launch {
            deleteQuizUseCase.deleteQuiz(quizId)
                .fold(
                    onSuccess = {
                        _quizEvents.send(Event.SnackbarEvent("Quiz deleted"))
                    },
                    onFailure = { error ->
                        _quizEvents.send(
                            Event.SnackbarEvent(
                                message = "Failed to delete quiz${error.message?.let { ": $it" }}"
                            )
                        )
                    }
                )
        }
    }
}