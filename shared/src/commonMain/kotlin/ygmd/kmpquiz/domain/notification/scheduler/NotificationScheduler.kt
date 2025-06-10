package ygmd.kmpquiz.domain.notification.scheduler

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.notification.ScheduledNotification

interface NotificationScheduler {
    fun getAllNotifications(): Flow<List<ScheduledNotification>>
    suspend fun scheduleNotification(notification: ScheduledNotification)
    suspend fun cancelNotification(notificationId: String)
}