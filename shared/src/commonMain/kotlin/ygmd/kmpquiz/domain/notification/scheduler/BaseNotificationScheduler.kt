package ygmd.kmpquiz.domain.notification.scheduler

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.notification.QandaNotification
import ygmd.kmpquiz.domain.repository.CronRepository
import ygmd.kmpquiz.domain.repository.notification.NotificationRepository
import ygmd.kmpquiz.domain.repository.qanda.QandaRepository

abstract class BaseNotificationScheduler(
    val notificationRepository: NotificationRepository,
    val cronRepository: CronRepository,
    val qandaRepository: QandaRepository,
) : NotificationScheduler {
    private var schedulerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    abstract suspend fun displayNotification(notification: QandaNotification)

    override fun getAllNotifications(): Flow<List<QandaNotification>> {
        return notificationRepository.getAllNotifications()
    }

    override suspend fun scheduleNotification(notification: QandaNotification) {
        notificationRepository.saveNotification(notification)


    }

    override suspend fun cancelNotification(notificationId: String) {
        TODO("Not yet implemented")
    }
}