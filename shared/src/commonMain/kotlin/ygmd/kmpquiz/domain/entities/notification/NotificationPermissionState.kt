package ygmd.kmpquiz.domain.entities.notification

data class NotificationPermissionState(
    val isGranted: Boolean,
    val shouldShowRationale: Boolean = false,
    val isPermanentlyDenied: Boolean = false
)