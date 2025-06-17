package ygmd.kmpquiz.data.repository.service.scheduler

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.entities.notification.ScheduledNotification

interface NotificationScheduler {
    fun getAllNotifications(): Flow<List<ScheduledNotification>>
    suspend fun scheduleNotification(notification: ScheduledNotification)
    suspend fun cancelNotification(notificationId: String)
}