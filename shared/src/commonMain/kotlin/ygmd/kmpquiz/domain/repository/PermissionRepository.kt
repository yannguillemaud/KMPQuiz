package ygmd.kmpquiz.domain.repository

interface PermissionRepository {
    suspend fun hasNotificationPermission(): Boolean
    suspend fun hasExactAlarmPermission(): Boolean
}