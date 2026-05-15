package ygmd.kmpquiz.domain.scheduler

interface QuizWorkManager {
    fun enqueueUniquePeriodicWork(
        workName: String,
        initialDelaySeconds: Long,
        repeatIntervalSeconds: Long,
        quizId: String,
        tag: String,
        isUserScheduleModification: Boolean = false,
    )

    fun cancelUniqueWork(uniqueWorkName: String)
    fun cancelAllWorkByTag(tag: String)
}
