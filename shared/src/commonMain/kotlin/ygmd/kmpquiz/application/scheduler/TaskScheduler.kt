package ygmd.kmpquiz.application.scheduler

import ygmd.kmpquiz.domain.entities.quiz.Quiz

interface TaskScheduler {
    suspend fun rescheduleQuizReminders(quizzes: List<Quiz>)
    suspend fun cancelAllReminders()
}