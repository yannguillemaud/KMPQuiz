package ygmd.kmpquiz.domain.repository

import kotlinx.coroutines.flow.Flow

interface SchedulerDataStore {
    val scheduledCrons: Flow<Map<String, Pair<String, Boolean>>> // Flow<Map<QuizID, Pair<CronExpression, IsEnabled>>>
    suspend fun updateScheduledCrons(newCrons: Map<String, Pair<String, Boolean>>)
    suspend fun clearAll()
}