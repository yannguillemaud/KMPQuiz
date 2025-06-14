package ygmd.kmpquiz.domain.repository.notification

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.pojo.notification.ScheduledNotification

interface ScheduledNotificationRepository {
    fun getAllScheduledNotifications(): Flow<List<ScheduledNotification>>
    suspend fun scheduleNotification(notification: ScheduledNotification)
    suspend fun cancelNotification(id: String)
    suspend fun markAsSent(id: String)
    suspend fun markAsFailed(id: String, reason: String)
    suspend fun getNotificationsForToday(): List<ScheduledNotification>
}