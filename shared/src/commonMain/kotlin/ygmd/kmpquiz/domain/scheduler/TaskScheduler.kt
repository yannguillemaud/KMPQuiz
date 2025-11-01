package ygmd.kmpquiz.domain.scheduler

import ygmd.kmpquiz.domain.model.cron.QuizCron
import ygmd.kmpquiz.domain.model.quiz.Quiz

interface TaskScheduler {
    suspend fun rescheduleQuizReminders(quizzes: List<Quiz>)
    suspend fun updateQuizReminder(quizId: String, newCronValue: QuizCron?)
    suspend fun cancelAllReminders()
}