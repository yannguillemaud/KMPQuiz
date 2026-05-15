package infra.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ygmd.kmpquiz.domain.notifier.QuizNotifier
import ygmd.kmpquiz.domain.repository.SchedulerDataStore
import ygmd.kmpquiz.domain.usecase.notification.ScheduleQuizUseCase

class QuizNotificationReceiver : BroadcastReceiver(), KoinComponent {

    private val scheduleQuizUseCase by inject<ScheduleQuizUseCase>()
    private val schedulerStore by inject<SchedulerDataStore>()
    private val quizNotifier by inject<QuizNotifier>()

    private val logger = Logger.withTag("QuizNotificationReceiver")

    companion object {
        const val QUIZ_ID_KEY = "quiz_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val quizId = intent.getStringExtra(QUIZ_ID_KEY)
        if (quizId == null) {
            logger.w { "Received alarm without quizId. Aborting." }
            return
        }
        logger.i { "Alarm triggered for quiz: $quizId" }
        quizNotifier.showQuizReminder(quizId)
        val pendingResult = goAsync()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        scope.launch {
            try {
                logger.d { "Fetching scheduler config for chaining next alarm..." }
                val configuration = schedulerStore.getConfiguration(quizId)
                if (configuration != null) {
                    scheduleQuizUseCase.schedule(quizId, configuration)
                    logger.i { "Successfully scheduled next occurrence for quiz: $quizId" }
                } else {
                    logger.d { "No configuration found or scheduler disabled. Stopping chain." }
                }
            } catch (e: Exception) {
                logger.e(e) { "Failed to chain next alarm for quiz: $quizId" }
            } finally {
                pendingResult.finish()
            }
        }
    }
}