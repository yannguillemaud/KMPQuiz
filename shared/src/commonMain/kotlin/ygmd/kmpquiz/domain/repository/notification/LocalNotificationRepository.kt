package ygmd.kmpquiz.domain.repository.notification

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import ygmd.kmpquiz.domain.notification.QandaNotification

class LocalNotificationRepository: NotificationRepository {
    private val _notifications = MutableStateFlow<MutableSet<QandaNotification>>(mutableSetOf())

    override fun getAllNotifications(): Flow<List<QandaNotification>> {
        return _notifications.map { it.toList() }
    }

    override suspend fun saveNotification(qandaNotification: QandaNotification) {
        _notifications.value += qandaNotification
    }

    override suspend fun deleteNotification(notificationId: Long) {
        _notifications.value = _notifications.value.apply {
            removeIf { it.id == notificationId }
        }
    }
}