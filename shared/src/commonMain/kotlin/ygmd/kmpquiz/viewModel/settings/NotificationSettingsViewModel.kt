package ygmd.kmpquiz.viewModel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ygmd.kmpquiz.application.usecase.notification.RescheduleTasksUseCase
import ygmd.kmpquiz.application.usecase.quiz.GetQuizUseCase
import ygmd.kmpquiz.application.usecase.quiz.UpdateQuizUseCase
import ygmd.kmpquiz.domain.entities.cron.CronExpression
import ygmd.kmpquiz.domain.entities.cron.QuizCron
import ygmd.kmpquiz.domain.entities.quiz.Quiz

private val logger = Logger.withTag("NotificationSettingsViewModel")

data class NotificationCronSettings(
    val cronsByQuiz: Map<Quiz, UiCronSetting> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

data class UiCronSetting(
    val title: String,
    val isEnabled: Boolean = false,
    val cronExpression: CronExpression,
){
    fun toCron(): QuizCron = QuizCron(
        title = title,
        cron = cronExpression,
        isEnabled = isEnabled
    )
}

class NotificationSettingsViewModel(
    private val getQuizUseCase: GetQuizUseCase,
    private val updateQuizUseCase: UpdateQuizUseCase,
    private val rescheduleTasksUseCase: RescheduleTasksUseCase,
) : ViewModel() {
    private var saveAndRescheduleJob: Job? = null
    private val _cronsSettings = MutableStateFlow(NotificationCronSettings())
    val cronSettingsState = _cronsSettings.asStateFlow()

    // Débounce pour éviter des sauvegardes/replanifications trop fréquentes
    private val debouncePeriodMillis = 500L

    init {
        loadCrons()
    }

    private fun loadCrons() {
        _cronsSettings.value = _cronsSettings.value.copy(isLoading = true)
        viewModelScope.launch {
            val quizzes = getQuizUseCase.getAllQuizz()
            logger.i { "Actual quizzes: ${quizzes.map { "${it.title}: ${it.quizCron?.isEnabled}" }}" }
            _cronsSettings.value = _cronsSettings.value.copy(
                isLoading = false,
                cronsByQuiz = quizzes
                    .mapNotNull { quiz -> quiz.quizCron?.let { quiz to it } }
                    .associateBy({ it.first }, { it.second })
                    .mapValues { (quiz, cron) ->
                        UiCronSetting(
                            title = quiz.title,
                            isEnabled = cron.isEnabled,
                            cronExpression = cron.cron
                        )
                    }
            )
        }
    }

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
            updateQuizUseCase.updateQuiz(quiz) { copy(quizCron = null) }
            _cronsSettings.value =
                _cronsSettings.value.copy(cronsByQuiz = _cronsSettings.value.cronsByQuiz - quiz)
        }
    }

    private fun onToggleCron(quiz: Quiz, cronSetting: UiCronSetting) {
        viewModelScope.launch {
            val value = !cronSetting.isEnabled
            quiz.quizCron?.let { cron ->
                updateQuizUseCase.updateQuiz(quiz) {
                    copy(quizCron = cron.copy(isEnabled = value))
                }
                updateCronSetting(quiz) {
                    copy(isEnabled = value)
                }
                logger.i { "${cronSetting.title} is now: $value" }
            }
        }
    }

    private fun onUpdateCron(quiz: Quiz, cronSetting: UiCronSetting) {
        viewModelScope.launch {
            quiz.quizCron?.let { cron ->
                updateQuizUseCase.updateQuiz(quiz) {
                    copy(
                        quizCron = cron.copy(
                            title = cronSetting.title,
                            cron = cronSetting.cronExpression
                        )
                    )
                }
                updateCronSetting(quiz) {
                    copy(
                        title = cronSetting.title,
                        cronExpression = cronSetting.cronExpression
                    )
                }
            }
        }
    }

    private fun updateCronSetting(quiz: Quiz, transform: UiCronSetting.() -> UiCronSetting) {
        val currentSettings = _cronsSettings.value
        val currentCrons = currentSettings.cronsByQuiz.toMutableMap()
        val existing = currentCrons[quiz] ?: return
        currentCrons[quiz] = existing.transform()
        _cronsSettings.value = currentSettings.copy(cronsByQuiz = currentCrons)
    }

}