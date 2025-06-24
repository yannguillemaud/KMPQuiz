package ygmd.kmpquiz.data.service

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import ygmd.kmpquiz.domain.entities.cron.CronExpression
import ygmd.kmpquiz.domain.service.CronExecutionCalculator

class SimpleCronCalculator: CronExecutionCalculator {
    override fun getNextExecution(
        cronExpression: CronExpression,
        from: Instant
    ): Instant? = when(this.toString()){
        "* * * * *" -> from.plus(1, DateTimeUnit.Companion.DAY, TimeZone.Companion.currentSystemDefault())
        else -> null // TODO
    }

    override fun getNextExecutions(
        cronExpression: CronExpression,
        count: Int,
        from: Instant
    ): List<Instant> {
        TODO("Not yet implemented")
    }
}