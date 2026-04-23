package ygmd.kmpquiz.domain.repository

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.model.cron.ScheduledCronState

interface SchedulerDataStore {
    val scheduledCrons: Flow<Map<String, ScheduledCronState>>
    suspend fun updateScheduledCrons(newCrons: Map<String, ScheduledCronState>)
    suspend fun clearAll()
}