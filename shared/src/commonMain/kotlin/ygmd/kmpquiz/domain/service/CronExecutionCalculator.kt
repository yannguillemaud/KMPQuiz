package ygmd.kmpquiz.domain.service

import kotlin.time.Duration

interface CronExecutionCalculator {

    fun getInterval(cronExpression: String): Duration
}