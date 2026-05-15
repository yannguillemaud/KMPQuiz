package scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import co.touchlab.kermit.Logger
import infra.receiver.QuizNotificationReceiver
import ygmd.kmpquiz.domain.scheduler.QuizScheduler
import kotlin.time.Instant

private val logger = Logger.withTag("AndroidScheduler")

class AndroidScheduler(
    private val context: Context
) : QuizScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun scheduleAlarm(quizId: String, exactTimestampEpochMillis: Long) {
        val pendingIntent = createPendingIntent(quizId)
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            exactTimestampEpochMillis,
            pendingIntent
        )
        logger.d {
            "Scheduled alarm for $quizId at ${
                Instant.fromEpochMilliseconds(
                    exactTimestampEpochMillis
                )
            }"
        }
    }

    override fun cancelAlarm(quizId: String) {
        val pendingIntent = createPendingIntent(quizId)
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun createPendingIntent(quizId: String): PendingIntent {
        val intent = Intent(context, QuizNotificationReceiver::class.java).apply {
            putExtra(QuizNotificationReceiver.QUIZ_ID_KEY, quizId)
        }
        return PendingIntent.getBroadcast(
            /* context = */ context,
            /* requestCode = */ quizId.hashCode(),
            /* intent = */ intent,
            /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}