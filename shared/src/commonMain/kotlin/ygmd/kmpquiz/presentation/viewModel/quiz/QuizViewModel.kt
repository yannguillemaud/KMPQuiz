package ygmd.kmpquiz.presentation.viewModel.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.core.domain.permission.PermissionOutcome
import ygmd.kmpquiz.core.service.permission.PermissionHandlerFactory
import ygmd.kmpquiz.core.usecase.scheduler.ToggleQuizSchedulerUseCase
import ygmd.kmpquiz.core.usecase.quiz.DeleteQuizUseCase
import ygmd.kmpquiz.core.usecase.quiz.GetQuizUseCase
import ygmd.kmpquiz.core.domain.event.Event
import ygmd.kmpquiz.core.domain.quiz.config.SchedulerSelection
import ygmd.kmpquiz.presentation.viewModel.displayable.DisplayableCategory

data class QuizzesUiState(
    val isLoading: Boolean = false,
    val quizzes: List<DisplayableQuiz> = emptyList(),
    val error: String? = null,
)

data class DisplayableQuiz(
    val id: String,
    val title : String = "",
    val questionsSize: Int = 0,
    val categories: List<DisplayableCategory> = emptyList(),
    val scheduler: SchedulerSelection? = null,
    val isScheduled: Boolean = false,
)

class QuizViewModel(
    private val getQuizUseCase: GetQuizUseCase,
    private val deleteQuizUseCase: DeleteQuizUseCase,
    private val toggleQuizSchedulerUseCase: ToggleQuizSchedulerUseCase,
    permissionHandlerFactory: PermissionHandlerFactory,
) : ViewModel() {
    private val _quizEvents = Channel<Event>()
    val quizEvents = _quizEvents.receiveAsFlow()
    // PermissionHandler is scope-bound (viewModelScope only exists once this ViewModel is
    // constructed), so it is built here from the injected factory rather than injected
    // directly — see PermissionHandlerFactory's kdoc.
    val notificationHandler = permissionHandlerFactory.create(viewModelScope)

    val quizzesState: StateFlow<QuizzesUiState> = getQuizUseCase.observeQuizzes()
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
                    isScheduled = quiz.isSchedulerEnabled
                )
            }
            QuizzesUiState(isLoading = false, quizzes = displayableQuizzes)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = QuizzesUiState(isLoading = true),
        )

    init {
        notificationHandler.refreshStatus()
    }

    fun toggleScheduler(quizId: String, wantsToEnable: Boolean) {
        viewModelScope.launch {
            if (!wantsToEnable) {
                toggleQuizSchedulerUseCase(quizId, false)
                return@launch
            }
            val outcome = notificationHandler.requestSuspend(
                rationaleMessage = "Notifications are required to schedule quizzes",
                settingsMessage = "Please enable notifications in Settings"
            )
            // Granted (Android, permission actually approved) or NotApplicable (desktop,
            // no OS permission gate) both mean "proceed" — anything else blocks.
            when (outcome) {
                is PermissionOutcome.Granted,
                is PermissionOutcome.NotApplicable -> toggleQuizSchedulerUseCase(quizId, true)
                is PermissionOutcome.Denied,
                is PermissionOutcome.DeniedAlways -> {
                    val uiEvent = Event.ShowSnackbar("Notifications are required to schedule quizzes")
                    _quizEvents.send(uiEvent)
                }
            }
        }
    }

    fun deleteQuiz(quizId: String) {
        viewModelScope.launch {
            val deleteResult = deleteQuizUseCase.deleteQuiz(quizId)
            val snackBarMessage =
                if (!deleteResult.isFailure) "Quiz deleted"
                else deleteResult.exceptionOrNull()?.message ?: "Failed to delete quiz"
            _quizEvents.send(Event.ShowSnackbar(snackBarMessage))
        }
    }
}