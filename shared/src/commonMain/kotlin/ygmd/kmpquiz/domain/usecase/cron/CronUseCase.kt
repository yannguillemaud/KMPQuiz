package ygmd.kmpquiz.domain.usecase.cron

import ygmd.kmpquiz.domain.repository.CronRepository
import ygmd.kmpquiz.domain.repository.QuizRepository
import ygmd.kmpquiz.domain.scheduler.TaskScheduler

class CronUseCase(
    private val quizRepository: QuizRepository,
    private val cronRepository: CronRepository,
    private val taskScheduler: TaskScheduler,
) {
    fun observeCrons() = cronRepository.observeCrons()
    suspend fun toggleCron(quizId: String, cronId: String, newValue: Boolean): Result<Unit> = try {
        quizRepository.toggleCron(quizId, newValue)
            .mapCatching {
                val quizCron = cronRepository.getCronById(cronId)
                    .mapCatching { it.copy(isEnabled = newValue) }
                    .getOrThrow()
                taskScheduler.updateQuizScheduling(
                    quizId = quizId,
                    newValue = quizCron
                )
            }.getOrThrow()
    } catch (e: Exception) {
        Result.failure(e)
    }
}