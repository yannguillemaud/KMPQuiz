package ygmd.kmpquiz.domain.service

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.entities.notification.Notification

interface NotificationService {
    /* PLANIFICATION */
    suspend fun scheduleNotificationForQanda(qandaId: Long): Result<Unit>
    suspend fun scheduleNotificationForCategory(category: String): Result<Unit>
    suspend fun scheduleAllNotifications(): Result<Unit>

    /* GESTION */
    suspend fun cancelNotificationForQanda(qandaId: Long): Result<Unit>
    suspend fun cancelNotificationForCategory(category: String): Result<Unit>
    suspend fun cancelAllNotifications(): Result<Unit>

    /* OBSERVABILITY */
    fun observeNotifications(): Flow<List<Notification>>
}