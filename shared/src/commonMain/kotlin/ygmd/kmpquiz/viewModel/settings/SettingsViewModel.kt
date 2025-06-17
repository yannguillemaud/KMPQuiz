package ygmd.kmpquiz.viewModel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import ygmd.kmpquiz.domain.entities.cron.CronExpression
import ygmd.kmpquiz.domain.entities.cron.CronPreset
import ygmd.kmpquiz.data.repository.notification.NotificationConfigRepository
import ygmd.kmpquiz.domain.usecase.GetQandasUseCase

class SettingsViewModel(
    getQandasUseCase: GetQandasUseCase,
    notificationConfigRepository: NotificationConfigRepository,
) : ViewModel() {

    val userSettings: StateFlow<CronSettings> = getQandasUseCase.execute()
        .combine(notificationConfigRepository.getNotificationConfig()) { savedQandas, configs ->
            val settings: Map<Long, CronExpression> = savedQandas
                .mapNotNull { qanda ->
                    qanda.id?.let { id ->
                        val categoryCron = configs.categoryCrons[qanda.category]?.cronExpression
                        val globalCron = configs.globalCron
                        val cronFallback = CronPreset.DAILY.toCronExpression()
                        id to (categoryCron ?: globalCron ?: cronFallback)
                    }
                }.toMap()

            CronSettings(settings)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CronSettings(emptyMap())
        )
}

data class CronSettings(
    val scheduledCrons: Map<Long, CronExpression>
) {
    val hasScheduledNotifications: Boolean get() = scheduledCrons.isNotEmpty()
    val scheduledCount: Int get() = scheduledCrons.size
}