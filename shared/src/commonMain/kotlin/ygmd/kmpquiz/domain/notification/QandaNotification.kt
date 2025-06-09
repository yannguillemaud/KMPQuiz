package ygmd.kmpquiz.domain.notification

import ygmd.kmpquiz.domain.pojo.InternalQanda
import java.time.LocalDateTime

data class QandaNotification(
    val id: Long,
    val title: String,
    val body: String,
    val scheduledTime: LocalDateTime,
    val qanda: InternalQanda,
)

data class NotificationPermissionState(
    val isGranted: Boolean,
    val shouldShowRationale: Boolean = false,
    val isPermanentlyDenied: Boolean = false
)