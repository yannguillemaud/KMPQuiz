package ygmd.kmpquiz.core.usecase.notification

import co.touchlab.kermit.Logger
import ygmd.kmpquiz.core.domain.notification.Notification
import ygmd.kmpquiz.core.repository.QuizRepository
import ygmd.kmpquiz.core.service.notification.NotificationHandler
import ygmd.kmpquiz.core.service.notification.NotificationSender
import ygmd.kmpquiz.core.service.scheduler.QuizAlarmScheduler
import ygmd.kmpquiz.core.usecase.scheduler.ComputeQuizSchedulerNextTriggerUseCase

private val logger = Logger.withTag("OnReceiveQuizNotificationUseCase")

class OnReceiveQuizNotificationUseCase(
    private val quizRepository: QuizRepository,
    private val quizAlarmScheduler: QuizAlarmScheduler,
    private val notificationSender: NotificationSender,
    private val computeQuizSchedulerNextTriggerUseCase: ComputeQuizSchedulerNextTriggerUseCase,
) : NotificationHandler {

    override suspend fun onReceive(notification: Notification) {
        notificationSender.notify(notification.quizId)
        handleQuizRescheduling(notification.quizId)
    }

    private suspend fun handleQuizRescheduling(quizId: String) {
        val quiz = quizRepository.getById(quizId).getOrNull() ?: run {
            logger.w { "Quiz $quizId not existing anymore." }
            return
        }
        if (quiz.schedulerConfiguration?.isEnabled == true) {
            computeQuizSchedulerNextTriggerUseCase.invoke(quiz)?.let {
                quizAlarmScheduler.scheduleAlarm(
                    quizId = quiz.id,
                    exactEpochMillis = it.toEpochMilliseconds()
                )
            }
        }
    }
}