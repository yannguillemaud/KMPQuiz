package ygmd.kmpquiz.domain.viewModel.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.brewkits.grant.AppGrant.NOTIFICATION
import dev.brewkits.grant.AppGrant.SCHEDULE_EXACT_ALARM
import dev.brewkits.grant.GrantHandler
import dev.brewkits.grant.GrantManager
import dev.brewkits.grant.GrantStatus.GRANTED
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.usecase.cron.ToggleQuizSchedulerUseCase
import ygmd.kmpquiz.domain.usecase.quiz.DeleteQuizUseCase
import ygmd.kmpquiz.domain.usecase.quiz.GetQuizUseCase
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableCategory
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableQuiz
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
    private val toggleQuizSchedulerUseCase: ToggleQuizSchedulerUseCase,
    private val grantManager: GrantManager,
) : ViewModel() {
    private val _quizEvents = Channel<Event>()
    val quizEvents = _quizEvents.receiveAsFlow()
    val notificationHandler = GrantHandler(grantManager, NOTIFICATION, viewModelScope)
    val exactAlarmHandler = GrantHandler(grantManager, SCHEDULE_EXACT_ALARM, viewModelScope)

    val quizzesState: StateFlow<QuizzesUiState> = getQuizUseCase.observeAll()
        .map { quizzes ->
            val displayableQuizzes = quizzes.map { quiz ->
                DisplayableQuiz(
                    id = quiz.id,
                    title = quiz.title,
                    categories = quiz.categories.map { category ->
                        DisplayableCategory(
                            id = category.id,
                            name = category.name,
                        )
                    },
                    questionsSize = quiz.questionsCount,
                    scheduler = quiz.schedulerConfiguration?.selection,
                    isScheduled = quiz.isSchedulerActive
                )
            }
            QuizzesUiState(isLoading = false, quizzes = displayableQuizzes)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = QuizzesUiState(isLoading = true),
        )

    fun toggleScheduler(quizId: String, wantsToEnable: Boolean) {
        viewModelScope.launch {
            if (!wantsToEnable) {
                toggleQuizSchedulerUseCase(quizId, false)
                return@launch
            }
            val result = notificationHandler.requestSuspend(
                rationaleMessage = "Notifications are required to schedule quizzes",
                settingsMessage = "Please enable notifications in Settings"
            )
            if (result == GRANTED) toggleQuizSchedulerUseCase(quizId, true)
            else {
                val uiEvent = Event.SnackbarEvent("Notifications are required to schedule quizzes")
                _quizEvents.send(uiEvent)
            }
        }
    }

    fun deleteQuiz(quizId: String) {
        viewModelScope.launch {
            val deleteResult = deleteQuizUseCase.deleteQuiz(quizId)
            val snackBarMessage =
                if (!deleteResult.isFailure) "Quiz deleted"
                else deleteResult.exceptionOrNull()?.message ?: "Failed to delete quiz"
            _quizEvents.send(Event.SnackbarEvent(snackBarMessage))
        }
    }
}