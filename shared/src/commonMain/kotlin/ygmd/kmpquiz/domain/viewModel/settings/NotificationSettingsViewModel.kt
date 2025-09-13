package ygmd.kmpquiz.domain.viewModel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.entities.cron.QuizCron
import ygmd.kmpquiz.domain.entities.quiz.Quiz
import ygmd.kmpquiz.domain.usecase.notification.RescheduleTasksUseCase
import ygmd.kmpquiz.domain.usecase.quiz.GetQuizUseCase
import ygmd.kmpquiz.domain.usecase.quiz.UpdateQuizUseCase
import ygmd.kmpquiz.domain.viewModel.error.UiEvent

private val logger = Logger.withTag("NotificationSettingsViewModel")

data class NotificationCronSettings(
    val cronsByQuiz: Map<Quiz, QuizCron> = emptyMap()
)

class NotificationSettingsViewModel(
    getQuizUseCase: GetQuizUseCase,
    private val updateQuizUseCase: UpdateQuizUseCase,
    private val rescheduleTasksUseCase: RescheduleTasksUseCase,
) : ViewModel() {
    private var saveAndRescheduleJob: Job? = null

    // Débounce pour éviter des sauvegardes/replanifications trop fréquentes
    private val debouncePeriodMillis = 500L

    private val _events = MutableSharedFlow<UiEvent>(replay = 1)
    val cronSettingsEvents: SharedFlow<UiEvent> = _events.asSharedFlow()

    val cronsState: StateFlow<NotificationCronSettings> =
        getQuizUseCase.observeAll()
            .map { quizzes ->
                val settings = quizzes
                    .mapNotNull { quiz -> quiz.quizCron?.let { cron -> quiz to cron } }
                    .associateBy({ it.first }, { it.second })
                NotificationCronSettings(settings)
            }.stateIn(
                scope = viewModelScope,
                started = WhileSubscribed(5000),
                initialValue = NotificationCronSettings()
            )


    fun dispatchIntent(intent: NotificationSettingsIntent) {
        when (intent) {
            is NotificationSettingsIntent.ToggleCron -> onToggleCron(
                intent.quiz,
                intent.cronSetting
            )

            is NotificationSettingsIntent.UpdateCron -> onUpdateCron(
                intent.quiz,
                intent.cronSetting
            )

            is NotificationSettingsIntent.DeleteCron -> onDeleteCron(intent.quiz)
        }

        triggerSaveAndReschedule()
    }

    private fun triggerSaveAndReschedule() {
        saveAndRescheduleJob?.cancel()
        saveAndRescheduleJob = viewModelScope.launch {
            delay(debouncePeriodMillis)
            rescheduleTasksUseCase.execute()
        }
    }

    private fun onDeleteCron(quiz: Quiz) {
        viewModelScope.launch {
            try{
                updateQuizUseCase.updateQuiz(quiz) { copy(quizCron = null) }
                _events.emit(UiEvent.Success("Cron for quiz is now removed"))
            } catch (e: Exception) {
                _events.emit(
                    UiEvent.Error(
                        message = e.message ?: "Could not delete cron for quiz ${quiz.id}",
                        action = null
                    )
                )
            }
        }
    }

    private fun onToggleCron(quiz: Quiz, cron: QuizCron) {
        viewModelScope.launch {
            try {
                updateQuizUseCase.updateQuiz(quiz, { copy(quizCron = cron) })
                _events.emit(UiEvent.Success("Cron for quiz is now ${cron.isEnabled}"))
            } catch (e: Exception) {
                _events.emit(
                    UiEvent.Error(
                        message = e.message ?: "Could not update cron for quiz ${quiz.id}",
                        action = null
                    )
                )
            }
        }
    }

    private fun onUpdateCron(quiz: Quiz, cronSetting: QuizCron) {
        viewModelScope.launch {
            try {
                quiz.quizCron?.let { cron ->
                    updateQuizUseCase.updateQuiz(quiz) {
                        copy(quizCron = cronSetting)
                    }
                    _events.emit(UiEvent.Success("Cron for quiz is now $cron"))
                }
            } catch (e: Exception) {
                _events.emit(
                    UiEvent.Error(
                        message = e.message ?: "Could not update cron for quiz ${quiz.id}",
                        action = null
                    )
                )
            }
        }
    }
}