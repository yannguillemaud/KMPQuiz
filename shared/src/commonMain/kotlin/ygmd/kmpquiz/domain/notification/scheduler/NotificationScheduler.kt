package ygmd.kmpquiz.domain.notification.scheduler

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.notification.QandaNotification

interface NotificationScheduler {
    fun getAllNotifications(): Flow<List<QandaNotification>>
    suspend fun scheduleNotification(notification: QandaNotification)
    suspend fun cancelNotification(notificationId: String)
}