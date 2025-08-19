package scheduler

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import co.touchlab.kermit.Logger
import worker.QuizReminderWorker
import ygmd.kmpquiz.application.scheduler.TaskScheduler
import ygmd.kmpquiz.domain.entities.cron.CronExpression
import ygmd.kmpquiz.domain.entities.quiz.Quiz
import ygmd.kmpquiz.domain.service.CronExecutionCalculator
import kotlin.time.toJavaDuration

// nom unique pour du worker de rappel de quiz
const val QUIZ_REMINDER_WORK_TAG = "quiz_reminder_work"

// Clé pour passer l'ID du quiz au Worker
const val QUIZ_ID_KEY = "quiz_id"

private val logger = Logger.withTag("AndroidTaskScheduler")

class AndroidTaskScheduler(
    private val workManager: WorkManager,
    private val cronExecutionCalculator: CronExecutionCalculator,
) : TaskScheduler {
    override suspend fun rescheduleQuizReminders(quizzes: List<Quiz>) {
        cancelAllReminders()

        logger.i { "Rescheduling quiz reminders" }
        quizzes
            .mapNotNull { quiz -> quiz.quizCron?.let { quiz to it } }
            .associateBy({ it.first }, { it.second })
            .forEach { quiz, cron ->
                enqueueReminder(quiz, cron.cron)
            }
    }

    override suspend fun cancelAllReminders() {
        workManager.cancelAllWorkByTag(QUIZ_REMINDER_WORK_TAG)
        logger.d { "Cancelled all previous quiz reminders with tag $QUIZ_REMINDER_WORK_TAG" }
    }

    private fun enqueueReminder(quiz: Quiz, cron: CronExpression) {
        val inputData = workDataOf(QUIZ_ID_KEY to quiz.id)
        val period = cronExecutionCalculator.getInterval(cron)
        if (period.inWholeMilliseconds > 0) {
            logger.d { "Scheduling quiz reminder for quiz ${quiz.id} in $period" }
            val reminderWorkRequest =
                PeriodicWorkRequestBuilder<QuizReminderWorker>(period.toJavaDuration()).apply {
                    setInputData(inputData)
                    addTag(QUIZ_REMINDER_WORK_TAG) // tag d'annulation
                }.build()
            workManager.enqueueUniquePeriodicWork(
                uniqueWorkName = QUIZ_REMINDER_WORK_TAG,
                existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.REPLACE,
                request = reminderWorkRequest
            )
        } else {
            logger.e { "Period is negative: $period" }
        }
    }
}