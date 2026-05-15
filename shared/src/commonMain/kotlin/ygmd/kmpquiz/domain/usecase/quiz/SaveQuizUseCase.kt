package ygmd.kmpquiz.domain.usecase.quiz

import co.touchlab.kermit.Logger
import ygmd.kmpquiz.domain.model.quiz.Quiz
import ygmd.kmpquiz.domain.repository.QuizRepository
import ygmd.kmpquiz.domain.usecase.notification.ScheduleQuizUseCase

private val logger = Logger.withTag("SaveQuizUseCase")

class SaveQuizUseCase(
    private val quizRepository: QuizRepository,
    private val scheduleQuizUseCase: ScheduleQuizUseCase
) {
    suspend fun save(quizToSave: Quiz): Result<Unit> {
        val existingQuiz = quizRepository.getQuizById(quizToSave.id).getOrNull()
        return quizRepository.saveQuiz(quizToSave).onSuccess {
            handleQuizScheduling(existingQuiz, quizToSave)
        }
    }

    private suspend fun handleQuizScheduling(previousQuiz: Quiz?, newQuiz: Quiz) {
        if (newQuiz.schedulerConfiguration == null || newQuiz.schedulerConfiguration.isEnabled.not())
            return
        if (previousQuiz == null || previousQuiz.schedulerConfiguration == null) {
            scheduleQuizUseCase.configureAndSchedule(newQuiz.id, newQuiz.schedulerConfiguration)
            return
        }
        if(scheduleQuizUseCase.isScheduled(newQuiz.id)){
            if(scheduleQuizUseCase.differs(newQuiz.id, newQuiz.schedulerConfiguration)){
                scheduleQuizUseCase.cancel(newQuiz.id)
            } else {
                logger.d { "Quiz already scheduled. Skipping scheduling." }
                return
            }
        }
        scheduleQuizUseCase.configureAndSchedule(newQuiz.id, newQuiz.schedulerConfiguration)
    }
}