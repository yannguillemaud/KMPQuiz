package ygmd.kmpquiz.core.scheduler

import co.touchlab.kermit.Logger
import ygmd.kmpquiz.core.service.scheduler.QuizAlarmScheduler

private val logger = Logger.withTag("NoopScheduler")

class NoopQuizAlarmScheduler : QuizAlarmScheduler {
    override fun scheduleAlarm(quizId: String, exactEpochMillis: Long): Result<Unit> {
        logger.e { "Alarm scheduling not handled in desktop." }
        return Result.failure(Exception("Not supported"))
    }

    override fun cancelAlarm(quizId: String) {}
}