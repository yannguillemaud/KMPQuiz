package ygmd.kmpquiz.domain.repository

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.model.cron.SchedulerConfiguration

interface SchedulerDataStore {
    val configurations: Flow<Map<String, SchedulerConfiguration>>
    suspend fun getConfiguration(quizId: String): SchedulerConfiguration?
    suspend fun saveConfiguration(quizId: String, configuration: SchedulerConfiguration)
    suspend fun removeConfiguration(quizId: String)
    suspend fun clearAll()
}