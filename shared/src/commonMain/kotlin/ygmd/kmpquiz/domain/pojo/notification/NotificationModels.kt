package ygmd.kmpquiz.domain.pojo.notification

import kotlinx.datetime.Instant
import ygmd.kmpquiz.domain.pojo.cron.CronExpression
import ygmd.kmpquiz.domain.pojo.cron.CronPreset

enum class NotificationPriority {
    LOW, NORMAL, HIGH
}

enum class NotificationStatus {
    PENDING, SENT, CANCELLED, FAILED
}

data class CategoryNotificationConfig(
    val isEnabled: Boolean,
    val cronExpression: CronExpression,
    val priority: NotificationPriority = NotificationPriority.NORMAL
)

data class NotificationConfig(
    val isEnabled: Boolean = true,
    val globalCron: CronExpression? = CronPreset.DAILY.toCronExpression(),
    val maxNotificationsPerDay: Int = 1,
    val categoryCrons: Map<String, CategoryNotificationConfig> = emptyMap()
)

data class ScheduledNotification(
    val id: String,
    val qandaId: Long,
    val scheduledTime: Instant,
    val cronExpression: CronExpression,
    val category: String,
    val status: NotificationStatus = NotificationStatus.PENDING
)