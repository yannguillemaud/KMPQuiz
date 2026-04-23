package ygmd.kmpquiz.domain.usecase

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import ygmd.kmpquiz.domain.model.cron.QuizCron
import ygmd.kmpquiz.domain.repository.CronRepository
import ygmd.kmpquiz.domain.repository.QuizRepository
import ygmd.kmpquiz.domain.scheduler.TaskScheduler
import ygmd.kmpquiz.domain.usecase.cron.CronUseCase
import kotlin.test.Test

class CronUseCaseTest {
    private val quizRepository = mock<QuizRepository> {}
    private val cronRepository = mock<CronRepository> {}
    private val taskScheduler = mock<TaskScheduler> {}
    private val useCase = CronUseCase(quizRepository, cronRepository, taskScheduler)

    @Test
    fun `cron updated should call taskScheduler`() = runTest {
        // Given
        val quizCron = QuizCron("id", "name", "expression", false)
        everySuspend { quizRepository.saveQuiz(any()) } returns Result.success(Unit)
        everySuspend { quizRepository.toggleCron(any(), any()) } returns Result.success(Unit)
        everySuspend { cronRepository.getCronById(any()) } returns Result.success(quizCron)

        // When
        useCase.toggleCron("quizId", "cronId", newValue = true)

        // Then
        verifySuspend {
            taskScheduler.updateQuizScheduling("quizId", newValue = quizCron)
        }
    }
}