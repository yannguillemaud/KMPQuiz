package ygmd.kmpquiz.core.repository

interface PermissionRepository {
    suspend fun hasNotificationPermission(): Boolean
    suspend fun hasExactAlarmPermission(): Boolean
}