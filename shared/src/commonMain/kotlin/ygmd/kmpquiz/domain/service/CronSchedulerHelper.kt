package ygmd.kmpquiz.domain.service

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toKotlinLocalDateTime
import ygmd.kmpquiz.domain.model.scheduler.SchedulerSelection
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import kotlin.time.Instant

object CronSchedulerHelper {
    fun computeNextTrigger(scheduler: SchedulerSelection.SpecificTime): Instant {
        val timeZone = TimeZone.currentSystemDefault()
        val zoneId = ZoneId.systemDefault()
        val nowLocalDateTime = LocalDateTime.now(zoneId)
        if (scheduler.localTime.isAfter(LocalTime.now(zoneId))) {
            val nexTrigger = nowLocalDateTime
                .withHour(scheduler.hour)
                .withMinute(scheduler.minute)
            return nexTrigger.toKotlinLocalDateTime().toInstant(timeZone)
        } else {
            val nexTrigger = nowLocalDateTime
                .plusDays(1)
                .withHour(scheduler.hour)
                .withMinute(scheduler.minute)
            return nexTrigger.toKotlinLocalDateTime().toInstant(timeZone)
        }
    }
}