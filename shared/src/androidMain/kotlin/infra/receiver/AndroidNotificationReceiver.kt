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
import ygmd.kmpquiz.core.domain.notification.Notification
import ygmd.kmpquiz.core.service.notification.NotificationHandler

private val logger = Logger.withTag("QuizNotificationReceiver")

class AndroidNotificationReceiver : BroadcastReceiver(), KoinComponent {

    private val notificationHandler by inject<NotificationHandler>()


    companion object {
        const val QUIZ_ID_KEY = "quiz_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val quizId = intent.getStringExtra(QUIZ_ID_KEY) ?: run {
            logger.w { "Received alarm without quizId. Aborting." }
            return
        }
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            notificationHandler.onReceive(Notification(quizId))
        }
    }
}