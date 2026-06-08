package ygmd.kmpquiz.domain.usecase.notification

import co.touchlab.kermit.Logger
import ygmd.kmpquiz.domain.model.scheduler.SchedulerConfiguration
import ygmd.kmpquiz.domain.usecase.quiz.GetQuizUseCase

private val logger = Logger.withTag("RescheduleTasksUseCase")

class RescheduleTaskUseCase(
    private val getQuizUseCase: GetQuizUseCase,
    private val scheduleQuizUseCase: ScheduleQuizUseCase,
) {
    // TODO - not working when app reboot - maybe should another way to determine if alarm is currently scheduled
    suspend fun rescheduleAll() {
        val schedulerConfigurationByQuiz: Map<String, SchedulerConfiguration> =
            getQuizUseCase.getAll()
                .mapNotNull { quiz -> quiz.schedulerConfiguration?.let { quiz.id to it } }
                .associateBy({ it.first }, { it.second })
        val quizzesToSchedule = schedulerConfigurationByQuiz.filterValues { it.isEnabled }
        quizzesToSchedule.forEach { (quizId, scheduler) ->
            logger.i { "Rescheduling quiz: $quizId" }
            scheduleQuizUseCase.schedule(quizId, scheduler.selection)
        }
    }
}