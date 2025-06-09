package ygmd.kmpquiz.domain.repository.notification

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.notification.QandaNotification

interface NotificationRepository {
    fun getAllNotifications(): Flow<List<QandaNotification>>
    suspend fun saveNotification(qandaNotification: QandaNotification)
    suspend fun deleteNotification(notificationId: Long)
}