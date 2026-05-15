package ygmd.kmpquiz.domain.scheduler

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime

interface QuizScheduler {
    fun scheduleAlarm(quizId: String, exactTimestampEpochMillis: Long)
    fun cancelAlarm(quizId: String)
}

interface TimeProvider {
    fun now(): LocalDateTime = java.time.LocalDateTime.now().toKotlinLocalDateTime()
}