package worker

import android.Manifest.permission.POST_NOTIFICATIONS
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

private val logger = Logger.withTag("QuizReminderWorker")

class QuizReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    private val notificationManager = NotificationManagerCompat.from(applicationContext)

    @RequiresPermission(POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        if (checkSelfPermission(
                applicationContext,
                POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            logger.w { "POST_NOTIFICATIONS permission not granted." }
            return Result.failure()
        }

        val quizId = inputData.getString(WorkRequestMetadataHeader.QUIZ_ID_KEY.value) ?: run {
            logger.e { "Worker failed: quizId is null." }
            return Result.failure()
        }

        logger.d { "Worker started reminder for quiz $quizId" }
        val intent = Intent(Intent.ACTION_VIEW, buildUriQuiz(quizId)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val notificationId = quizId.hashCode()
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification =
            NotificationCompat.Builder(applicationContext, ReminderWorkerConst.CHANNEL_ID)
                .apply {
                    setSmallIcon(android.R.drawable.ic_dialog_info)
                    setContentTitle("Quiz time !")
//                    setContentText("$quizTitle is waiting for you")
                    setContentIntent(pendingIntent)
                    setAutoCancel(true)
                    priority = NotificationCompat.PRIORITY_DEFAULT
                }.build()

        notificationManager.notify(notificationId, notification)
        logger.i { "Notification for quiz $quizId has been posted." }
        return Result.success()
    }
}

fun buildUriQuiz(quizId: String) = "myapp://quiz/$quizId".toUri()