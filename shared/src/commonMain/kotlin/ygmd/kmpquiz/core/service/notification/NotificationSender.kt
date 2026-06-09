package ygmd.kmpquiz.core.service.notification

interface NotificationSender {
    fun notify(quizId: String)
}