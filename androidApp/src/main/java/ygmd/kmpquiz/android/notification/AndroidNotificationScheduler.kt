package ygmd.kmpquiz.android.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import ygmd.kmpquiz.domain.entities.notification.ScheduledNotification

class AndroidNotificationScheduler(
    private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleNotification(notification: ScheduledNotification) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("notification_id", notification.id)
            putExtra("qanda_id", notification.qandaId)
            putExtra("question", notification.category) // On peut ajouter d'autres données
        }

        val requestCode = notification.id.hashCode()
        val flags =
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getBroadcast(
            /* context = */ context,
            /* requestCode = */ requestCode,
            /* intent = */ intent,
            /* flags = */ flags
        )

        val timestampMillis = notification.scheduledTime.toEpochMilliseconds()

        alarmManager.setExactAndAllowWhileIdle(
            /* type = */ AlarmManager.RTC_WAKEUP,
            /* triggerAtMillis = */ timestampMillis,
            /* operation = */ pendingIntent
        )
    }

    fun cancelNotification(notificationId: String) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val requestCode = notificationId.hashCode()
        val flags =
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            flags
        )

        // Annuler l'alarme
        alarmManager.cancel(pendingIntent)
    }
}