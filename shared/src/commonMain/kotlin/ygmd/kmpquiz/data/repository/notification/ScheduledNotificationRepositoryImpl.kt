package ygmd.kmpquiz.data.repository.notification

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ygmd.kmpquiz.domain.entities.notification.NotificationStatus
import ygmd.kmpquiz.domain.entities.notification.ScheduledNotification

class ScheduledNotificationRepositoryImpl(
    private val logger: Logger
) : ScheduledNotificationRepository {
    private val _notifications = MutableStateFlow<List<ScheduledNotification>>(emptyList())

    override fun getAllScheduledNotifications(): Flow<List<ScheduledNotification>> =
        _notifications.asStateFlow()

    override suspend fun scheduleNotification(notification: ScheduledNotification) {
        logger.i { "Adding ${notification.id} to notifications" }
        _notifications.value += notification
    }

    override suspend fun cancelNotification(id: String) {
        notificationsOrNull(id)?.let {
            val newNotifications = _notifications.value.toMutableList()
            newNotifications -= it
            _notifications.value = newNotifications
            logger.i { "Removed notification $id" }
        }
    }

    override suspend fun markAsSent(id: String) {
        notificationsOrNull(id)?.let {
            _notifications.value.map { notification ->
                if(notification.id == id) notification.copy(status = NotificationStatus.SENT)
                else notification
            }
            logger.i { "Marked notification $id as sent" }
        }
    }

    override suspend fun markAsFailed(id: String, reason: String) {
        notificationsOrNull(id)?.let {
            _notifications.value.map { notification ->
                if(notification.id == id) notification.copy(status = NotificationStatus.FAILED)
                else notification
            }
            logger.i { "Marked notification $id as failed: $reason" }
        }
    }

    override suspend fun getNotificationsForToday(): List<ScheduledNotification> =
        _notifications.value.filter {
            it.scheduledTime
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date == Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        }

    private fun notificationsOrNull(id: String): ScheduledNotification? = _notifications.value
        .firstOrNull { it.id == id }
        ?: run {
            logger.w { "Notification $id not found in notifications" }
            null
        }
}