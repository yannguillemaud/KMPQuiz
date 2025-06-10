package ygmd.kmpquiz.viewModel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.cron.CronPreset
import ygmd.kmpquiz.domain.notification.CategoryNotificationConfig
import ygmd.kmpquiz.domain.notification.NotificationConfig
import ygmd.kmpquiz.domain.repository.notification.NotificationConfigRepository
import ygmd.kmpquiz.domain.usecase.GetQandasUseCase
import ygmd.kmpquiz.domain.usecase.NotificationUseCase

class NotificationSettingsViewModel(
    private val configRepository: NotificationConfigRepository,
    private val getQandasUseCase: GetQandasUseCase,
    private val notificationUseCase: NotificationUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val uiState: StateFlow<NotificationSettingsUiState> = combine(
        configRepository.getNotificationConfig(),
        getQandasUseCase.execute()
    ) { config, qandas ->
        val categories = qandas.map { it.category }.distinct()
        NotificationSettingsUiState(
            config = config,
            availableCategories = categories
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NotificationSettingsUiState()
    )

    fun toggleGlobalNotifications(enabled: Boolean) {
        viewModelScope.launch {
            configRepository.toggleGlobalNotifications(enabled)
        }
    }

    fun updateGlobalCron(cronPreset: CronPreset) {
        viewModelScope.launch {
            configRepository.updateGlobalCron(cronPreset.toCronExpression())
        }
    }

    fun setCategoryCron(category: String, cronPreset: CronPreset, enabled: Boolean = true) {
        viewModelScope.launch {
            val config = CategoryNotificationConfig(
                isEnabled = enabled,
                cronExpression = cronPreset.toCronExpression()
            )
            configRepository.setCategoryCron(category, config)
        }
    }

    fun removeCategoryCron(category: String) {
        viewModelScope.launch {
            configRepository.removeCategoryCron(category)
        }
    }

    fun applyChanges() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                notificationUseCase.generateAndScheduleNotifications()
            } finally {
                _isLoading.value = false
            }
        }
    }
}

data class NotificationSettingsUiState(
    val config: NotificationConfig = NotificationConfig(),
    val availableCategories: List<String> = emptyList()
)