package ygmd.kmpquiz.infra.scheduler

/**
 * Possible actions for the scheduler
 */
sealed interface SchedulerAction {
    val quizId: String
    data class Cancel(override val quizId: String) : SchedulerAction
    data class Enqueue(override val quizId: String, val cron: String) : SchedulerAction
    data class NoOp(override val quizId: String) : SchedulerAction
}