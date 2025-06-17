package ygmd.kmpquiz.domain.usecase

import kotlinx.coroutines.flow.first
import ygmd.kmpquiz.data.repository.service.NotificationService

interface NotificationUseCase {
    suspend fun generateAndScheduleNotifications()
    suspend fun cancelAllNotifications()
}

class NotificationUseCaseImpl(
    private val notificationService: NotificationService
) : NotificationUseCase {

    override suspend fun generateAndScheduleNotifications() {
        notificationService.generateNotificationsFromConfig()
    }

    override suspend fun cancelAllNotifications() {
        notificationService.getAllScheduledNotifications().first()
            .forEach { notificationService.cancelNotification(it.id) }
    }
}