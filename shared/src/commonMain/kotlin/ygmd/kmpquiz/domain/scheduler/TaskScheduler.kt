package ygmd.kmpquiz.domain.scheduler

import ygmd.kmpquiz.domain.model.cron.QuizCron
import ygmd.kmpquiz.domain.model.quiz.Quiz

interface TaskScheduler {
    suspend fun rescheduleAllQuizzes(quizzes: List<Quiz>)
    suspend fun rescheduleQuiz(quizId: String, newCronValue: QuizCron?)
    suspend fun cancelAllReminders()
}