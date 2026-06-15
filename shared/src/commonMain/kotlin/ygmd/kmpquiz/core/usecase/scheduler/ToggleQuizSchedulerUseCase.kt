package ygmd.kmpquiz.core.usecase.scheduler

import ygmd.kmpquiz.core.repository.PermissionRepository
import ygmd.kmpquiz.core.repository.QuizRepository
import ygmd.kmpquiz.core.usecase.notification.ScheduleQuizUseCase

class ToggleQuizSchedulerUseCase(
    private val quizRepository: QuizRepository,
    private val scheduleQuizUseCase: ScheduleQuizUseCase,
    private val permissionRepository: PermissionRepository,
) {
    suspend operator fun invoke(quizId: String, isEnabled: Boolean): Result<Unit> =
        quizRepository.toggleQuizScheduler(quizId, isEnabled)
            .onSuccess {
                if (!isEnabled) {
                    scheduleQuizUseCase.cancel(quizId)
                    return@onSuccess
                }
                if (!permissionRepository.hasNotificationPermission()) {
                    return Result.failure(Exception("Notification permission is not granted"))
                }
                scheduleQuizUseCase.schedule(quizId)
            }
}