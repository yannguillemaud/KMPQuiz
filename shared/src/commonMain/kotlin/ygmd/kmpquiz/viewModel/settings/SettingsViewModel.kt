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
import ygmd.kmpquiz.domain.repository.qanda.QandaRepository

class SettingsViewModel(
    qandaRepository: QandaRepository,
    cronRepository: CronRepository,
) : ViewModel() {
    val userSettings: StateFlow<CronSettings> = qandaRepository.getAll()
        .combine(cronRepository.getCrons()) { saved, crons ->
            val settings: Map<Long, CronExpression> = saved.associateBy({it.id!!}) {
                crons[it.id] ?: CronPreset.DAILY.toCronExpression()
            }
            CronSettings(settings)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CronSettings(emptyMap())
        )
}