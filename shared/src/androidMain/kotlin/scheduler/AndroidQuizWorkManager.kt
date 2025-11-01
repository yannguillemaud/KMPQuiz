package scheduler

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.workDataOf
import co.touchlab.kermit.Logger
import worker.QuizReminderWorker
import worker.WorkRequestMetadataHeader
import ygmd.kmpquiz.domain.model.cron.ScheduledCrons
import ygmd.kmpquiz.domain.scheduler.QuizWorkManager
import ygmd.kmpquiz.infra.scheduler.ScheduledCronsSerializer
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlin.time.toJavaDuration
import androidx.work.WorkManager as AndroidWorkManager

private const val SCHEDULED_CRONS_DATASTORE_FILE_NAME_ANDROID = "scheduled_crons_platform.json"
val Context.platformScheduledCronsDataStore: DataStore<ScheduledCrons> by dataStore(
    fileName = SCHEDULED_CRONS_DATASTORE_FILE_NAME_ANDROID,
    serializer = ScheduledCronsSerializer
)

private val logger = Logger.withTag("AndroidQuizWorkManager")

class AndroidQuizWorkManager(
    private val workManager: AndroidWorkManager,
) : QuizWorkManager {
    override fun enqueueUniquePeriodicWork(
        workName: String,
        initialDelaySeconds: Long,
        repeatIntervalSeconds: Long,
        quizId: String,
        tag: String,
    ) {
        if (repeatIntervalSeconds <= 0) return

        if (initialDelaySeconds > 0) {
            logger.i {
                "InitialDelaySeconds ($initialDelaySeconds) is specified for PeriodicWork for $workName. " +
                "Note: Standard PeriodicWorkRequest's first run is after the first repeat interval. " +
                "True initial delay requires a more complex setup (e.g., OneTimeWork chaining)."
            }
        }

        val inputData = workDataOf(
            WorkRequestMetadataHeader.QUIZ_ID_KEY.value to quizId,
        )

        try {
            val periodicWorkRequest = PeriodicWorkRequestBuilder<QuizReminderWorker>(
                repeatIntervalSeconds
                    .toDuration(DurationUnit.SECONDS)
                    .plus(1.seconds)
                    .toJavaDuration()
            ).apply {
                setConstraints(androidx.work.Constraints.Builder().build())
                setInputData(inputData)
                addTag(tag)
            }.build()

            logger.d {
                "Enqueuing periodic work: Name='$workName', Interval='${repeatIntervalSeconds}s', QuizID='$quizId', Tag='$tag'"
            }

            workManager.enqueueUniquePeriodicWork(
                uniqueWorkName = workName,
                existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.UPDATE,
                request = periodicWorkRequest
            )
        } catch (e: Exception) {
            logger.e(e) {
                "Failed to build or enqueue PeriodicWorkRequest for $workName"
            }
        }
    }

    override fun cancelUniqueWork(uniqueWorkName: String) {
        logger.withTag("AndroidQuizWorkManagerImpl")
            .d { "Cancelling unique work: Name='$uniqueWorkName'" }
        workManager.cancelUniqueWork(uniqueWorkName)
    }

    override fun cancelAllWorkByTag(tag: String) {
        logger.withTag("AndroidQuizWorkManagerImpl")
            .d { "Cancelling all work with tag: Tag='$tag'" }
        workManager.cancelAllWorkByTag(tag)
    }
}