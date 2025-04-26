package scheduler

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ygmd.kmpquiz.domain.model.cron.CronDetails
import ygmd.kmpquiz.domain.model.cron.ScheduledCrons
import ygmd.kmpquiz.domain.repository.SchedulerDataStore

class AndroidSchedulerDataStore(
    private val dataStore: DataStore<ScheduledCrons> // Votre DataStore existant
) : SchedulerDataStore {
    override val scheduledCrons: Flow<Map<String, Pair<String, Boolean>>>
        get() = dataStore.data.map { stored ->
            stored.crons.mapValues { entry ->
                Pair(entry.value.cronExpression, entry.value.isEnabled)
            }
        }

    override suspend fun updateScheduledCrons(newCrons: Map<String, Pair<String, Boolean>>) {
        dataStore.updateData {
            val detailsMap = newCrons.mapValues { entry ->
                CronDetails(entry.value.first, entry.value.second)
            }
            ScheduledCrons(crons = detailsMap)
        }
    }

    override suspend fun clearAll() {
        dataStore.updateData { ScheduledCrons() }
    }
}