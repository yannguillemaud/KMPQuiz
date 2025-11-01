package ygmd.kmpquiz.data.service

import com.ucasoft.kcron.Cron
import com.ucasoft.kcron.core.builders.DelicateIterableApi
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import ygmd.kmpquiz.domain.model.cron.CronExpression
import ygmd.kmpquiz.domain.service.CronExecutionCalculator
import kotlin.time.Duration
import kotlin.time.toKotlinDuration

class SimpleCronCalculator : CronExecutionCalculator {
    @OptIn(DelicateIterableApi::class)
    override fun getInterval(cronExpression: CronExpression): Duration {
        val instants = Cron.parseAndBuild(cronExpression.expression)
            .asIterable(from = java.time.LocalDateTime.now().toKotlinLocalDateTime())
            .take(2)
            .map { it.toJavaLocalDateTime() }
        return java.time.Duration
            .between(instants.first(), instants.last())
            .toKotlinDuration()
    }
}