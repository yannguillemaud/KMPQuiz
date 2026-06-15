package ygmd.kmpquiz.core.service.notification

import ygmd.kmpquiz.core.domain.notification.Notification


interface NotificationHandler {
    suspend fun onReceive(notification: Notification)
}