package ygmd.kmpquiz.viewModel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ygmd.kmpquiz.application.usecase.cron.GetCronUseCase
import ygmd.kmpquiz.application.usecase.notification.GetNotificationUseCase
import ygmd.kmpquiz.application.usecase.qanda.GetQandasUseCase
import ygmd.kmpquiz.domain.entities.cron.Cron
import ygmd.kmpquiz.domain.entities.cron.CronPreset
import ygmd.kmpquiz.domain.entities.qanda.Qanda

private val logger = Logger.withTag("NotificationSettingsViewModel")

class NotificationSettingsViewModel(
    private val getQandasUseCase: GetQandasUseCase,
    private val cronUseCase: GetCronUseCase,
    private val notificationUseCase: GetNotificationUseCase,
) : ViewModel() {

    private val _qandas: Flow<List<Qanda>> = getQandasUseCase.observeAll()
    private val _cron: Flow<List<Cron>> = cronUseCase.observeAll()
    private val _notificationUiState = MutableStateFlow(NotificationCronSettings())

    val notificationUiState = _notificationUiState.asStateFlow()

    init {
        viewModelScope.launch { loadNotificationCrons() }
    }

    private suspend fun loadNotificationCrons() {
        val globalCronExpression =
            cronUseCase.getGlobalCron()?.expression ?: CronPreset.DAILY.toCronExpression()
        val globalCron = Cron(id = "global_cron", expression = globalCronExpression)
        _notificationUiState.value = NotificationCronSettings(globalCron = globalCron)
        /* TODO */
    }

    fun updateGlobalCron(cron: Cron){
        _notificationUiState.value = _notificationUiState.value.copy(
            globalCron = cron
        )
        logger.i { "Global cron is now set to ${cron.expressionAsText}" }
    }
}

data class NotificationCronSettings(
    val globalCron: Cron? = null,
    val notificationCronMap: Map<String, Cron> = emptyMap()
)