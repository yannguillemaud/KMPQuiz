package ygmd.kmpquiz.data.repository.notification

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.entities.cron.CronExpression
import ygmd.kmpquiz.domain.entities.notification.CategoryNotificationConfig
import ygmd.kmpquiz.domain.entities.notification.NotificationConfig

interface NotificationConfigRepository {
    fun getNotificationConfig(): Flow<NotificationConfig>
    suspend fun updateNotificationConfig(config: NotificationConfig)

    // Méthodes helper
    suspend fun toggleGlobalNotifications(enabled: Boolean)
    suspend fun updateGlobalCron(cronExpression: CronExpression)
    suspend fun setCategoryCron(category: String, config: CategoryNotificationConfig)
    suspend fun removeCategoryCron(category: String)
    suspend fun toggleCategoryNotifications(category: String, enabled: Boolean)
}