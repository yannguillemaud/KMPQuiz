package ygmd.kmpquiz.domain.notification

data class NotificationPermissionState(
    val isGranted: Boolean,
    val shouldShowRationale: Boolean = false,
    val isPermanentlyDenied: Boolean = false
)