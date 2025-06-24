package ygmd.kmpquiz.domain.entities.notification

import kotlinx.datetime.Instant
import ygmd.kmpquiz.domain.entities.cron.CronExpression

enum class NotificationStatus {
    PENDING,
    SENT,
    CANCELLED,
    FAILED
}

data class Notification(
    val id: String,
    val qandaId: Long,
    val scheduledTime: Instant,
    val cronExpression: CronExpression,
    val title: String,
    val body: String?,
    val status: NotificationStatus = NotificationStatus.PENDING
)