package ygmd.kmpquiz.domain.usecase.cron

import co.touchlab.kermit.Logger
import ygmd.kmpquiz.domain.repository.QuizRepository

private val logger = Logger.withTag("ToggleCronUseCase")

class ToggleCronUseCase(
    private val quizRepository: QuizRepository
) {
    suspend fun toggleCron(quizId: String, newValue: Boolean): Result<Unit> = try {
        val updated = quizRepository.getQuizById(quizId)
            .getOrThrow()
            .let {
                it.copy(quizCron = it.quizCron?.copy(isEnabled = newValue))
            }
        quizRepository.saveQuiz(quizId, updated)
    } catch (e: Exception){
        logger.e(e) { "Failed to toggle cron for quiz $quizId" }
        Result.failure(e)
    }
}