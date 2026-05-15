package ygmd.kmpquiz.data.repository.scheduler

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import ygmd.kmpquiz.domain.model.cron.SchedulerConfiguration
import ygmd.kmpquiz.domain.repository.SchedulerDataStore

private val logger = Logger.withTag("SchedulerDataStore")

class SchedulerDataStoreImpl(
    private val dataStore: DataStore<Preferences>,
    private val json: Json = Json { ignoreUnknownKeys = true }
): SchedulerDataStore {
    companion object {
        private val SCHEDULERS_KEY = stringPreferencesKey("quiz_schedulers_map")
    }

    override val configurations: Flow<Map<String, SchedulerConfiguration>> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { decodeMap(it[SCHEDULERS_KEY]) }

    override suspend fun saveConfiguration(quizId: String, configuration: SchedulerConfiguration) {
        dataStore.edit { preferences ->
            val currentMap = decodeMap(preferences[SCHEDULERS_KEY]).toMutableMap()
            currentMap[quizId] = configuration
            preferences[SCHEDULERS_KEY] = json.encodeToString(currentMap)
        }
    }

    override suspend fun removeConfiguration(quizId: String) {
        dataStore.edit { preferences ->
            val currentMap = decodeMap(preferences[SCHEDULERS_KEY]).toMutableMap()
            if (currentMap.remove(quizId) != null) {
                preferences[SCHEDULERS_KEY] = json.encodeToString(currentMap)
            }
        }
    }

    override suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.remove(SCHEDULERS_KEY)
        }
    }

    override suspend fun getConfiguration(quizId: String): SchedulerConfiguration? {
        return configurations.first()[quizId]
    }

    private fun decodeMap(jsonString: String?): Map<String, SchedulerConfiguration> {
        if (jsonString.isNullOrBlank()) return emptyMap()
        return try {
            json.decodeFromString<Map<String, SchedulerConfiguration>>(jsonString)
        } catch (e: Exception) {
            logger.e(e){ "Failed to decode map" }
            emptyMap()
        }
    }
}