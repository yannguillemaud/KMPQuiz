package infra.notifier

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import ygmd.kmpquiz.domain.notifier.QuizNotifier

class AndroidQuizNotifier(
    private val context: Context
) : QuizNotifier {

    companion object {
        private const val CHANNEL_ID = "quiz_reminders_channel"
        private const val DEEP_LINK_SCHEME = "myapp"
        private const val DEEP_LINK_HOST = "quiz"
    }

    init {
        createNotificationChannel()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun showQuizReminder(quizId: String) {
        if (!hasNotificationPermission()) return

        val notificationManager = NotificationManagerCompat.from(context)
        val pendingIntent = createPendingIntent(quizId)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            // todo - use drawable vector
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Quiz Time !")
            .setContentText("Click and start quiz")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(quizId.hashCode(), notification)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Quiz_Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminders for quizzes"
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createPendingIntent(quizId: String): PendingIntent {
        val componentName = Class.forName("ygmd.kmpquiz.android.MainActivity")
        val tapIntent = Intent(context, componentName).apply {
            action = Intent.ACTION_VIEW
            data = "$DEEP_LINK_SCHEME://$DEEP_LINK_HOST/$quizId".toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        return PendingIntent.getActivity(
            context,
            quizId.hashCode(),
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }
        return true
    }
}