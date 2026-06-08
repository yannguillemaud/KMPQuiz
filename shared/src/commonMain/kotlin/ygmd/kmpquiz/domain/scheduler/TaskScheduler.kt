package ygmd.kmpquiz.domain.scheduler

interface QuizScheduler {
    /**
     * Schedules an alarm for the given quiz id at the given timestamp
     * @param exactTimestampEpochMillis the timestamp in milliseconds
     */
    fun scheduleAlarm(quizId: String, exactTimestampEpochMillis: Long)

    /**
     * Cancels the alarm for the given quiz id
     */
    fun cancelAlarm(quizId: String)
}