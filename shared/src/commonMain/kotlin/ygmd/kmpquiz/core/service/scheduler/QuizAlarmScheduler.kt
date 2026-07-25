package ygmd.kmpquiz.core.service.scheduler

interface QuizAlarmScheduler {
    /**
     * Schedules an alarm for the given quiz id at the given timestamp
     * @param exactEpochMillis the timestamp in milliseconds
     */
    fun scheduleAlarm(quizId: String, exactEpochMillis: Long): Result<Unit>

    /**
     * Cancels the alarm for the given quiz id
     */
    fun cancelAlarm(quizId: String)
}