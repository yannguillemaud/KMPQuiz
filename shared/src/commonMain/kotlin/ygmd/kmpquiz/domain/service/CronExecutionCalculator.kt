package ygmd.kmpquiz.domain.service

import ygmd.kmpquiz.domain.entities.cron.CronExpression
import kotlin.time.Duration

interface CronExecutionCalculator {

    fun getInterval(cronExpression: CronExpression): Duration
}