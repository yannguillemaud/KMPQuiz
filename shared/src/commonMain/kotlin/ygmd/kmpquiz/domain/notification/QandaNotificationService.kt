package ygmd.kmpquiz.domain.notification

import ygmd.kmpquiz.domain.notification.scheduler.NotificationScheduler
import ygmd.kmpquiz.domain.pojo.InternalQanda
import ygmd.kmpquiz.domain.repository.qanda.QandaRepository

class QandaNotificationService(
    private val notificationScheduler: NotificationScheduler,
    private val qandaRepository: QandaRepository,
) {
    suspend fun scheduleQuizNotification(internalQanda: InternalQanda){
        if(!notificationScheduler.hasPermission())
    }
}