package ygmd.kmpquiz.domain.scheduler

import ygmd.kmpquiz.domain.model.cron.QuizCron
import ygmd.kmpquiz.domain.model.quiz.Quiz

interface TaskScheduler {
    suspend fun scheduleAllQuizzes(quizzes: List<Quiz>)
    suspend fun updateQuizScheduling(quizId: String, newValue: QuizCron?): Result<Unit>
    suspend fun cancelAllReminders()
}