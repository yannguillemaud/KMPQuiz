package worker

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.touchlab.kermit.Logger
import scheduler.QUIZ_ID_KEY

private val logger = Logger.withTag("QuizReminderWorker")

class QuizReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        if (checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            logger.w { "POST_NOTIFICATIONS permission not granted." }
            return Result.failure()
        }

        val quizId = inputData.getString(QUIZ_ID_KEY) ?: return Result.failure()
        logger.i { "Worker started reminder for quiz $quizId" }

        val intent = Intent(
            Intent.ACTION_VIEW,
            "myapp://quiz/$quizId".toUri()
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            quizId.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        val notification = NotificationCompat.Builder(applicationContext, "quiz_notifications")
            .apply {
                setSmallIcon(android.R.drawable.ic_dialog_info)
                setContentTitle("C'est l'heure de jouer !")
                setContentText("Prêt pour ton quiz $quizId ?")
                setContentIntent(pendingIntent)
                setAutoCancel(true)
                priority = NotificationCompat.PRIORITY_DEFAULT
            }.build()
        notificationManager.notify(quizId.hashCode(), notification)
        return Result.success()
    }
}