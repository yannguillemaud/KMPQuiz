package ygmd.kmpquiz.viewModel.settings

import ygmd.kmpquiz.domain.cron.CronExpression

data class CronSettings(
    val scheduledCrons: Map<Long, CronExpression>
)