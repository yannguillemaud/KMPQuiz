package ygmd.kmpquiz.domain.usecase.notification

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.first
import ygmd.kmpquiz.domain.repository.SchedulerDataStore

private val logger = Logger.withTag("RescheduleTasksUseCase")

class RescheduleAllQuizzesUseCase(
    private val dataStore: SchedulerDataStore,
    private val scheduleQuizUseCase: ScheduleQuizUseCase
) {
    suspend operator fun invoke() {
        logger.d { "Rescheduling all quizzes" }
        dataStore.configurations.first()
            .filterValues { it.isEnabled }
            .forEach(scheduleQuizUseCase::schedule)
    }
}