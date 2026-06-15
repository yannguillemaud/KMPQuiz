package ygmd.kmpquiz

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jetbrains.annotations.TestOnly
import ygmd.kmpquiz.core.domain.quiz.config.QuizSchedulerConfiguration

@TestOnly
class FakeSchedulerStore : SchedulerDataStore {
    private val _configurations = MutableStateFlow(mapOf<String, QuizSchedulerConfiguration>())

    override val configurations: Flow<Map<String, QuizSchedulerConfiguration>>
        get() = _configurations.asStateFlow()

    override suspend fun getConfiguration(quizId: String): QuizSchedulerConfiguration? {
        return _configurations.value[quizId]
    }

    override suspend fun saveConfiguration(
        quizId: String,
        configuration: QuizSchedulerConfiguration
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

    override suspend fun exists(quizId: String): Boolean {
        return _configurations.value.containsKey(quizId)
    }
}
