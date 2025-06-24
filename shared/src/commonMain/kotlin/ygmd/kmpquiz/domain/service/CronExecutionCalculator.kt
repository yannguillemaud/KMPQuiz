package ygmd.kmpquiz.domain.service

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import ygmd.kmpquiz.domain.entities.cron.CronExpression

interface CronExecutionCalculator {
    fun getNextExecution(
        cronExpression: CronExpression,
        from: Instant = Clock.System.now()
    ): Instant?

    fun getNextExecutions(
        cronExpression: CronExpression,
        count: Int,
        from: Instant = Clock.System.now()
    ): List<Instant>
}