package ygmd.kmpquiz.viewModel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import ygmd.kmpquiz.domain.cron.CronExpression
import ygmd.kmpquiz.domain.cron.CronPreset
import ygmd.kmpquiz.domain.repository.cron.CronRepository
import ygmd.kmpquiz.domain.usecase.GetQandasUseCase

class SettingsViewModel(
    getQandasUseCase: GetQandasUseCase,
    cronRepository: CronRepository,
) : ViewModel() {

    val userSettings: StateFlow<CronSettings> = getQandasUseCase.execute()
        .combine(cronRepository.getCrons()) { savedQandas, crons ->
            val settings: Map<Long, CronExpression> = savedQandas
                .mapNotNull { qanda -> qanda.id }
                .associateWith { qandaId ->
                    crons[qandaId] ?: CronPreset.DAILY.toCronExpression()
                }
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