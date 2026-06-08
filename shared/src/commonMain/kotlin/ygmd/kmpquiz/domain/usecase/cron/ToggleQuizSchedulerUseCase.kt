package ygmd.kmpquiz.domain.usecase.cron

import ygmd.kmpquiz.domain.repository.PermissionRepository
import ygmd.kmpquiz.domain.repository.QuizRepository
import ygmd.kmpquiz.domain.usecase.notification.ScheduleQuizUseCase

class ToggleQuizSchedulerUseCase(
    private val quizRepository: QuizRepository,
    private val scheduleQuizUseCase: ScheduleQuizUseCase,
    private val permissionRepository: PermissionRepository,
) {
    suspend operator fun invoke(quizId: String, isEnabled: Boolean): Result<Unit> {
        val config = quizRepository.getById(quizId).getOrNull()?.schedulerConfiguration
            ?: return Result.failure(
                NoSchedulerForQuizException(quizId)
            )
        return quizRepository.toggleQuizScheduler(quizId, isEnabled)
            .onSuccess {
                if (!isEnabled) {
                    scheduleQuizUseCase.cancel(quizId)
                    return@onSuccess
                }
                if (!permissionRepository.hasNotificationPermission()) {
                    return Result.failure(NoNotificationPermission())
                }
                scheduleQuizUseCase.schedule(quizId, config.selection)
            }
    }
}

class NoNotificationPermission : Exception("Cannot schedule quiz without exact alarm permission")

data class NoSchedulerForQuizException(val quizId: String) : Exception(
    "Cannot schedule quiz $quizId without configuration"
)