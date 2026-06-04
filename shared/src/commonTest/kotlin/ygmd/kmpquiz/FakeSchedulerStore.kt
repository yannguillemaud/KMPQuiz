package ygmd.kmpquiz

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jetbrains.annotations.TestOnly
import ygmd.kmpquiz.domain.model.scheduler.SchedulerConfiguration
import ygmd.kmpquiz.domain.repository.SchedulerDataStore

@TestOnly
class FakeSchedulerStore : SchedulerDataStore {
    private val _configurations = MutableStateFlow(mapOf<String, SchedulerConfiguration>())

    override val configurations: Flow<Map<String, SchedulerConfiguration>>
        get() = _configurations.asStateFlow()

    override suspend fun getConfiguration(quizId: String): SchedulerConfiguration? {
        return _configurations.value[quizId]
    }

    override suspend fun saveConfiguration(
        quizId: String,
        configuration: SchedulerConfiguration
    ) {
        _configurations.value = _configurations.value.toMutableMap().apply {
            put(quizId, configuration)
        }
    }

    override suspend fun removeConfiguration(quizId: String) {
        _configurations.value = _configurations.value.toMutableMap().apply {
            remove(quizId)
        }
    }

    override suspend fun clearAll() {
        _configurations.value = emptyMap()
    }
}
