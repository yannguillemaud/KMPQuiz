package ygmd.kmpquiz.data.service

import com.ucasoft.kcron.Cron
import com.ucasoft.kcron.core.builders.DelicateIterableApi
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import ygmd.kmpquiz.domain.service.CronExecutionCalculator
import kotlin.time.Duration
import kotlin.time.toKotlinDuration

class SimpleCronCalculator : CronExecutionCalculator {
    @OptIn(DelicateIterableApi::class)
    override fun getInterval(cronExpression: String): Duration {
        val instants = Cron.parseAndBuild(cronExpression)
            .asIterable(from = java.time.LocalDateTime.now().toKotlinLocalDateTime())
            .take(2)
            .map { it.toJavaLocalDateTime() }
        return java.time.Duration
            .between(instants.first(), instants.last())
            .toKotlinDuration()
    }
}