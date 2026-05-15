package ygmd.kmpquiz.domain.service

import com.ucasoft.kcron.Cron
import com.ucasoft.kcron.core.builders.DelicateIterableApi
import com.ucasoft.kcron.core.extensions.anyDays
import com.ucasoft.kcron.core.extensions.hours
import com.ucasoft.kcron.core.extensions.minutes
import com.ucasoft.kcron.cron
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDateTime
import ygmd.kmpquiz.domain.model.cron.SchedulerSelection
import kotlin.time.Duration
import kotlin.time.toKotlinDuration

@OptIn(DelicateIterableApi::class)
object CronSchedulerHelper {
    fun durationOfCron(cronExpression: String): Duration {
        val instants = Cron.parseAndBuild(cronExpression)
            .asIterable()
            .take(2)
            .map { it.toJavaLocalDateTime() }
        return java.time.Duration
            .between(instants.first(), instants.last())
            .toKotlinDuration()
    }

    fun nextExecutionAsMillis(cronExpression: String): Long {
        return Cron.parseAndBuild(cronExpression)
            .nextRun
            ?.toInstant(TimeZone.currentSystemDefault())
            ?.toEpochMilliseconds() ?: error("Cannot find next execution for cron: $cronExpression")
    }

    fun selectionAsCron(selection: SchedulerSelection): String {
        val cronExpression = when (selection) {
            is SchedulerSelection.SpecificTime -> {
                cron {
                    minutes(selection.minute)
                    hours(selection.hour)
                    anyDays()
                }.expression
            }

            is SchedulerSelection.TimeRange -> {
                TODO()
            }
        }
        return cronExpression
    }
}