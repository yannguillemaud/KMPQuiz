package ygmd.kmpquiz.domain.repository

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.entities.notification.Notification

interface NotificationRepository {
    fun observeAllNotifications(): Flow<List<Notification>>

    suspend fun getAllNotifications(): List<Notification>
    suspend fun scheduleNotification(notification: Notification)
    suspend fun cancelNotification(id: String)
    suspend fun markAsSent(id: String)
    suspend fun markAsFailed(id: String, reason: String)
    suspend fun getNotificationsForToday(): List<Notification>
}