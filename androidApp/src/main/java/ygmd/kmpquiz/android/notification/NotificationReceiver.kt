package ygmd.kmpquiz.android.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import ygmd.kmpquiz.android.MainActivity

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        android.util.Log.d("NotificationReceiver", "onReceive called with action=${intent.action}")
        when(intent.action){
            "QUIZ_REMINDER" -> handleQuizReminder(context, intent)
            "QUIZ_ACTION" -> handleQuizAction(context, intent)
        }
    }

    private fun handleQuizReminder(context: Context, intent: Intent){
        val quizId = intent.getStringExtra("quiz_id") ?: return
        val sessionId = intent.getStringExtra("session_id")
        val quizTitle = intent.getStringExtra("quiz_title") ?: "Quiz"

        // Intent qui ouvre directement ton activité Main
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("quiz_id", quizId)
            putExtra("session_id", sessionId)
            putExtra("open_quiz", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construire la notification
        val notification = NotificationCompat.Builder(context, NotificationUtils.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("C'est l'heure de jouer !")
            .setContentText("Prêt pour ton quiz : $quizTitle ?")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent) // ouvre Main directement
            .setAutoCancel(true)
            .build()

        // Afficher
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1001, notification)
    }

    private fun handleQuizAction(context: Context, intent: Intent) {
        val quizId = intent.getStringExtra("quiz_id") ?: return
        val sessionId = intent.getStringExtra("session_id")

        // Ici tu rediriges vers ton mécanisme de lancement de quiz
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("quiz_id", quizId)
            putExtra("session_id", sessionId)
            putExtra("open_quiz", true)
        }
        context.startActivity(launchIntent)
    }
}