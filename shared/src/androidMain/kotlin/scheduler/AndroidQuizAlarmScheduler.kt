package scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import co.touchlab.kermit.Logger
import infra.receiver.AndroidNotificationReceiver
import ygmd.kmpquiz.core.service.scheduler.QuizAlarmScheduler
import java.time.Instant
import java.time.ZoneId

private val logger = Logger.withTag("AndroidScheduler")

class AndroidAlarmScheduler(
    private val context: Context
) : QuizAlarmScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun scheduleAlarm(quizId: String, exactEpochMillis: Long) {
        val pendingIntent = createPendingIntent(quizId)
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            exactEpochMillis,
            pendingIntent
        )
        logger.d {
            "Scheduled alarm for $quizId at ${
                Instant.ofEpochMilli(exactEpochMillis).atZone(ZoneId.systemDefault())
            }"
        }
    }

    override fun cancelAlarm(quizId: String) {
        val targetIntent = createPendingIntent(quizId)
        targetIntent.cancel()
        logger.i { "Canceled alarm for quiz $quizId" }
    }

    private fun createPendingIntent(quizId: String): PendingIntent {
        val intent = Intent(context, AndroidNotificationReceiver::class.java).apply {
            putExtra(AndroidNotificationReceiver.QUIZ_ID_KEY, quizId)
        }
        return PendingIntent.getBroadcast(
            /* context = */ context,
            /* requestCode = */ quizId.hashCode(),
            /* intent = */ intent,
            /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}