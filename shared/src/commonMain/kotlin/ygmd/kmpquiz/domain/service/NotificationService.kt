package ygmd.kmpquiz.domain.service

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import ygmd.kmpquiz.domain.cron.CronExpression
import ygmd.kmpquiz.domain.cron.CronPreset
import ygmd.kmpquiz.domain.notification.NotificationStatus
import ygmd.kmpquiz.domain.notification.ScheduledNotification
import ygmd.kmpquiz.domain.repository.notification.NotificationConfigRepository
import ygmd.kmpquiz.domain.repository.notification.ScheduledNotificationRepository
import ygmd.kmpquiz.domain.repository.qanda.QandaRepository

interface NotificationService {
    fun getAllScheduledNotifications(): Flow<List<ScheduledNotification>>
    suspend fun generateNotificationsFromConfig()
    suspend fun scheduleNotification(notification: ScheduledNotification)
    suspend fun cancelNotification(id: String)
}

class NotificationServiceImpl(
    private val scheduledRepo: ScheduledNotificationRepository,
    private val configRepo: NotificationConfigRepository,
    private val qandaRepo: QandaRepository,
    private val logger: Logger
) : NotificationService {

    override fun getAllScheduledNotifications(): Flow<List<ScheduledNotification>> {
        return scheduledRepo.getAllScheduledNotifications()
    }

    override suspend fun generateNotificationsFromConfig() {
        val config = configRepo.getNotificationConfig().first()
        if (config.isEnabled.not()) return

        val qandas = qandaRepo.getAll().first()

        val qandaNotifications = qandas.mapNotNull { qanda ->
            qanda.id?.let { id ->
                val cronExpression = config.categoryCrons[qanda.category]?.cronExpression
                    ?: config.globalCron
                    ?: CronPreset.DAILY.toCronExpression()

                val notificationId = "${qanda.contentKey}_${System.currentTimeMillis()}"

                ScheduledNotification(
                    id = notificationId,
                    qandaId = qanda.id!!,
                    scheduledTime = calculateNextExecutionTime(cronExpression),
                    cronExpression = cronExpression,
                    status = NotificationStatus.PENDING,
                    category = qanda.category
                )
            }
        }
        qandaNotifications.forEach { scheduleNotification(it) }
        logger.i { "Generated ${qandaNotifications.size} notifications from config" }
    }

    private fun calculateNextExecutionTime(cronExpression: CronExpression): Instant {
        return Clock.System.now().plus(DateTimePeriod(days = 1), TimeZone.currentSystemDefault())
    }

    override suspend fun scheduleNotification(notification: ScheduledNotification) {
        scheduledRepo.scheduleNotification(notification)
    }

    override suspend fun cancelNotification(id: String) {
        scheduledRepo.cancelNotification(id)
    }
}