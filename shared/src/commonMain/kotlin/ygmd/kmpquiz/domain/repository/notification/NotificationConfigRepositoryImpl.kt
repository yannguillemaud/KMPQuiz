package ygmd.kmpquiz.domain.repository.notification

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ygmd.kmpquiz.domain.pojo.cron.CronExpression
import ygmd.kmpquiz.domain.pojo.notification.CategoryNotificationConfig
import ygmd.kmpquiz.domain.pojo.notification.NotificationConfig

class NotificationConfigRepositoryImpl(private val logger: Logger) : NotificationConfigRepository {
    private val _notifications = MutableStateFlow(NotificationConfig())

    override fun getNotificationConfig(): Flow<NotificationConfig> = _notifications.asStateFlow()

    override suspend fun updateNotificationConfig(config: NotificationConfig) {
        logger.i { "Updating config: $config" }
        _notifications.value = config
    }

    override suspend fun toggleGlobalNotifications(enabled: Boolean) {
        val status = if (enabled) "enabled" else "disabled"
        logger.i { "Global notifications is now $status" }

        _notifications.value = _notifications.value.copy(isEnabled = enabled)
    }

    override suspend fun updateGlobalCron(cronExpression: CronExpression) {
        logger.i { "Global cron set to $cronExpression" }
        _notifications.value = _notifications.value.copy(globalCron = cronExpression)
    }

    override suspend fun setCategoryCron(category: String, config: CategoryNotificationConfig) {
        logger.i { "Set cron for $category to $config" }
        val newCategoryCrons = _notifications.value.categoryCrons + (category to config)
        _notifications.value = _notifications.value.copy(categoryCrons = newCategoryCrons)
    }

    override suspend fun removeCategoryCron(category: String) {
        val currentCrons = _notifications.value
            .categoryCrons
            .toMutableMap()
        val existingCronForCategory = currentCrons.remove(category)

        if (existingCronForCategory == null) {
            logger.w { "Category $category not found in existing config" }
            return
        }
        _notifications.value = _notifications.value.copy(categoryCrons = currentCrons)
        logger.i { "Successfully removed $category out of config" }
    }

    override suspend fun toggleCategoryNotifications(category: String, enabled: Boolean) {
        val currentCrons = _notifications.value.categoryCrons
        val existingCron = currentCrons[category]
        if (existingCron == null) {
            logger.e { "Category $category not found in existing config" }
            return
        }
        val newConfigForCategory = existingCron.copy(isEnabled = enabled)
        _notifications.value = _notifications.value.copy(
            categoryCrons = currentCrons + (category to newConfigForCategory)
        )
        logger.i { "Category $category is now ${if (enabled) "enabled" else "disabled"}" }
    }
}