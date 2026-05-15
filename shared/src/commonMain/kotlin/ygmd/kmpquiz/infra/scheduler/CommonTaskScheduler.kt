package ygmd.kmpquiz.infra.scheduler

import co.touchlab.kermit.Logger

private val logger = Logger.withTag("CommonTaskScheduler")
//
//class CommonTaskScheduler(
//    private val quizWorkManager: QuizWorkManager,
//    private val schedulerDataStoreV1: SchedulerDataStoreV1,
//) : TaskScheduler {
//
//    companion object {
//        const val WORK_TAG = "quiz_reminder_work"
//        private const val WORK_PREFIX = "quiz_reminder_work_"
//
//        private fun getWorkName(id: String) = "$WORK_PREFIX$id"
//    }
//
//    override suspend fun scheduleQuizzes(schedulersByQuiz: Map<String, SchedulerConfiguration>) {
//        try {
//            val storedStates = schedulerDataStoreV1.scheduledCrons.first().toMutableMap()
//            var dataStoreNeedsUpdate = false
//            schedulersByQuiz.forEach { (quizId, config) ->
//                val existingState = storedStates[quizId]
//                val isNew = existingState == null
//                val hasChanged =
//                    existingState?.expression != config.cron || existingState.isEnabled != config.isEnabled
//                if (isNew || hasChanged) {
//                    if (config.isEnabled) {
//                        executeWorkAction(SchedulerAction.Enqueue(quizId, config.cron))
//                    } else {
//                        executeWorkAction(SchedulerAction.Cancel(quizId))
//                    }
//                    storedStates[quizId] = ScheduledCronState(config.cron, config.isEnabled)
//                    dataStoreNeedsUpdate = true
//                }
//            }
//            if (dataStoreNeedsUpdate) {
//                schedulerDataStoreV1.updateScheduledCrons(storedStates)
//                logger.i { "Startup sync complete: missing or outdated schedulers were updated." }
//            } else {
//                logger.d { "Startup sync complete: everything is already up to date." }
//            }
//
//        } catch (e: Exception) {
//            logger.e(e) { "Failed to synchronize quizzes at startup" }
//        }
//    }
//
//    private fun executeWorkAction(action: SchedulerAction) {
//        when (action) {
//            is SchedulerAction.Cancel -> {
//                quizWorkManager.cancelUniqueWork(getWorkName(action.quizId))
//                logger.d { "Cancelled: ${action.quizId}" }
//            }
//
//            is SchedulerAction.Enqueue -> {
//            }
//
//            is SchedulerAction.Repeat -> {
//                val interval = CronSchedulerHelper.durationOfCron(action.cron)
//                if (interval.isPositive()) {
//                    quizWorkManager.enqueueUniquePeriodicWork(
//                        workName = getWorkName(action.quizId),
//                        initialDelaySeconds = 0,
//                        repeatIntervalSeconds = interval.inWholeSeconds,
//                        quizId = action.quizId,
//                        tag = WORK_TAG
//                    )
//                    logger.d { "Enqueued: ${action.quizId} (Interval: ${interval.inWholeMinutes}m)" }
//                }
//
//            }
//
//            is SchedulerAction.NoOp -> Unit
//        }
//    }
//
//    override suspend fun setScheduler(
//        quizId: String,
//        schedulerConfiguration: SchedulerConfiguration
//    ) {
//        try {
//            val expression = schedulerConfiguration.cron
//            val isEnabled = schedulerConfiguration.isEnabled
//            val storedStates = schedulerDataStoreV1.scheduledCrons.first().toMutableMap()
//            storedStates[quizId] = ScheduledCronState(expression, isEnabled)
//            schedulerDataStoreV1.updateScheduledCrons(storedStates)
//            logger.d { "Configuration saved for quiz $quizId: enabled=$isEnabled" }
//            if (isEnabled) scheduleQuiz(quizId)
//            else cancelScheduler(quizId)
//        } catch (e: Exception) {
//            logger.e(e) { "Failed to set scheduler configuration for quiz $quizId" }
//        }
//    }
//
//    override suspend fun scheduleQuiz(quizId: String): Result<Unit> {
//        return try {
//            val storedStates = schedulerDataStoreV1.scheduledCrons.first()
//            val config = storedStates[quizId]
//            if (config != null && config.isEnabled) {
//                val action = SchedulerAction.Enqueue(quizId, config.expression)
//                executeWorkAction(action)
//                logger.d { "Quiz $quizId successfully scheduled." }
//                Result.success(Unit)
//            } else {
//                val msg = "Cannot schedule quiz $quizId: Configuration is missing or disabled."
//                logger.w { msg }
//                Result.failure(IllegalStateException(msg))
//            }
//        } catch (e: Exception) {
//            logger.e(e) { "Error while scheduling quiz $quizId" }
//            Result.failure(e)
//        }
//    }
//
//    override suspend fun cancelScheduler(quizId: String): Result<Unit> {
//        try {
//            executeWorkAction(SchedulerAction.Cancel(quizId))
//            val storedStates = schedulerDataStoreV1.scheduledCrons.first().toMutableMap()
//            if (storedStates.containsKey(quizId)) {
//                storedStates.remove(quizId)
//                schedulerDataStoreV1.updateScheduledCrons(storedStates)
//            }
//            logger.d { "Scheduler successfully cancelled and removed for quiz: $quizId" }
//            return Result.success(Unit)
//        } catch (e: Exception) {
//            logger.e(e) { "Failed to cancel scheduler for quiz $quizId" }
//            return Result.failure(e)
//        }
//    }
//}