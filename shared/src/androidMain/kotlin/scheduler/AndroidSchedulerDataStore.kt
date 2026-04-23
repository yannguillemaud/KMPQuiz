package scheduler

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ygmd.kmpquiz.domain.model.cron.ScheduledCronState
import ygmd.kmpquiz.domain.model.cron.ScheduledCrons
import ygmd.kmpquiz.domain.repository.SchedulerDataStore

class AndroidSchedulerDataStore(
    private val dataStore: DataStore<ScheduledCrons>
) : SchedulerDataStore {
    override val scheduledCrons: Flow<Map<String, ScheduledCronState>>
        get() = dataStore.data.map { stored ->
            stored.crons.mapValues { (_, state) ->
                val (expression, isEnabled) = state
                ScheduledCronState(expression, isEnabled)
            }
        }

    override suspend fun updateScheduledCrons(newCrons: Map<String, ScheduledCronState>) {
        dataStore.updateData {
            val detailsMap = newCrons.mapValues { (_, state) ->
                ScheduledCronState(state.expression, state.isEnabled)
            }
            ScheduledCrons(crons = detailsMap)
        }
    }

    override suspend fun clearAll() {
        dataStore.updateData { ScheduledCrons() }
    }
}