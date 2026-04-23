package ygmd.kmpquiz.infra.scheduler

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.first
import ygmd.kmpquiz.domain.model.cron.QuizCron
import ygmd.kmpquiz.domain.model.cron.ScheduledCronState
import ygmd.kmpquiz.domain.model.quiz.Quiz
import ygmd.kmpquiz.domain.repository.SchedulerDataStore
import ygmd.kmpquiz.domain.scheduler.QuizWorkManager
import ygmd.kmpquiz.domain.scheduler.TaskScheduler
import ygmd.kmpquiz.domain.service.CronExecutionCalculator

private val logger = Logger.withTag("CommonTaskScheduler")

class CommonTaskScheduler(
    private val quizWorkManager: QuizWorkManager,
    private val schedulerDataStore: SchedulerDataStore,
    private val cronExecutionCalculator: CronExecutionCalculator,
) : TaskScheduler {

    companion object {
        const val WORK_TAG = "quiz_reminder_work"
        private const val WORK_PREFIX = "quiz_reminder_work_"

        private fun getWorkName(id: String) = "$WORK_PREFIX$id"
    }

    /**
     * sync method
     */
    override suspend fun scheduleAllQuizzes(quizzes: List<Quiz>) {
        val storedStates = schedulerDataStore.scheduledCrons.first()
        val newQuizzesById = quizzes.associateBy { it.id }

        // Union des IDs pour traiter Ajouts, Updates et Suppressions
        val allIds = storedStates.keys + newQuizzesById.keys
        val newStatesToStore = mutableMapOf<String, ScheduledCronState>()

        allIds.forEach { id ->
            val action = determineAction(
                quizId = id,
                newConfig = newQuizzesById[id]?.cron,
                storedState = storedStates[id]
            )

            executeWorkAction(action)
            newQuizzesById[id]?.cron?.let {
                newStatesToStore[id] = ScheduledCronState(it.expression, it.isEnabled)
            }
        }

        schedulerDataStore.updateScheduledCrons(newStatesToStore)
        logger.i { "Global resync complete. ${newStatesToStore.size} quizzes tracked." }
    }

    /**
     * if newCron null -> remove
     */
    override suspend fun updateQuizScheduling(quizId: String, newValue: QuizCron?): Result<Unit> {
        return try {
            val storedStates = schedulerDataStore.scheduledCrons.first().toMutableMap()
            val action = determineAction(
                quizId = quizId,
                newConfig = newValue,
                storedState = storedStates[quizId]
            )
            logger.d { "Action to perform: $action" }
            executeWorkAction(action)
            if (newValue != null) {
                storedStates[quizId] = ScheduledCronState(newValue.expression, newValue.isEnabled)
            } else {
                storedStates.remove(quizId)
            }
            schedulerDataStore.updateScheduledCrons(storedStates)
            logger.d { "Updated: $quizId" }
            Result.success(Unit)
        } catch (e: Exception) {
            logger.e(e) { "Error updating quiz $quizId" }
            Result.failure(e)
        }
    }

    private fun determineAction(
        quizId: String,
        newConfig: QuizCron?,
        storedState: ScheduledCronState?
    ): SchedulerAction {
        val shouldBeActive = newConfig?.isEnabled == true
        val wasActive = storedState?.isEnabled == true
        val cronChanged = newConfig?.expression != storedState?.expression

        return when {
            newConfig == null || !shouldBeActive -> {
                if (wasActive) SchedulerAction.Cancel(quizId)
                else SchedulerAction.NoOp(quizId)
            }
            cronChanged || !wasActive -> {
                SchedulerAction.Enqueue(quizId, newConfig.expression)
            }
            else -> SchedulerAction.NoOp(quizId)
        }
    }

    private fun executeWorkAction(action: SchedulerAction) {
        when (action) {
            is SchedulerAction.Cancel -> {
                quizWorkManager.cancelUniqueWork(getWorkName(action.quizId))
                logger.d { "Cancelled: ${action.quizId}" }
            }
            is SchedulerAction.Enqueue -> {
                val interval = cronExecutionCalculator.getInterval(action.cron)
                if (interval.isPositive()) {
                    quizWorkManager.enqueueUniquePeriodicWork(
                        workName = getWorkName(action.quizId),
                        initialDelaySeconds = 0,
                        repeatIntervalSeconds = interval.inWholeSeconds,
                        quizId = action.quizId,
                        tag = WORK_TAG
                    )
                    logger.d { "Enqueued: ${action.quizId} (Interval: ${interval.inWholeMinutes}m)" }
                }
            }
            is SchedulerAction.NoOp -> Unit
        }
    }

    override suspend fun cancelAllReminders() {
        quizWorkManager.cancelAllWorkByTag(WORK_TAG)
        schedulerDataStore.clearAll()
    }
}