package ygmd.kmpquiz.domain.service

import ygmd.kmpquiz.domain.model.cron.CronExpression
import kotlin.time.Duration

interface CronExecutionCalculator {

    fun getInterval(cronExpression: CronExpression): Duration
}