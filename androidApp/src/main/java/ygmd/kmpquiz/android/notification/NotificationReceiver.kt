package ygmd.kmpquiz.android.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import ygmd.kmpquiz.android.Main

class NotificationReceiver : BroadcastReceiver() {
    companion object {
        private const val CHANNEL_ID = "quiz_notifications"
        private const val CHANNEL_NAME = "Quizs"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onReceive(context: Context, intent: Intent) {
        val notificationid = intent.getStringExtra("notification_id") ?: return
        val qandaId = intent.getLongExtra("qanda_id", -1L)
        val category = intent.getStringExtra("category") ?: "Quiz"

        showNotification(context, notificationid, qandaId, category)
    }

    private fun showNotification(
        context: Context,
        notificationid: String,
        qandaId: Long,
        category: String
    ) {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(notificationManager)

        val intent = Intent(context, Main::class.java).apply {
            putExtra("qanda_id", qandaId)
            putExtra("notification_id", notificationid)
            putExtra("category", category)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            qandaId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setContentTitle("🧠 Quiz time on $category !")
            setContentText("Une nouvelle question t'attend !")
            setSmallIcon(android.R.drawable.ic_dialog_info)
            setContentIntent(pendingIntent)
            setAutoCancel(true)
        }

        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications pour les quiz programmés"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}