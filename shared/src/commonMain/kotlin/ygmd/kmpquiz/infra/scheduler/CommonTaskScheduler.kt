package ygmd.kmpquiz.infra.scheduler

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.first
import ygmd.kmpquiz.domain.model.cron.CronExpression
import ygmd.kmpquiz.domain.model.cron.QuizCron
import ygmd.kmpquiz.domain.model.quiz.Quiz
import ygmd.kmpquiz.domain.repository.SchedulerDataStore
import ygmd.kmpquiz.domain.scheduler.QuizWorkManager
import ygmd.kmpquiz.domain.scheduler.TaskScheduler
import ygmd.kmpquiz.domain.service.CronExecutionCalculator
import ygmd.kmpquiz.infra.scheduler.Action.Cancel
import ygmd.kmpquiz.infra.scheduler.Action.Enqueue
import ygmd.kmpquiz.infra.scheduler.Action.NoChange

private const val LOG_TAG = "CommonTaskScheduler"

/**
 * Implémentation multiplateforme de [TaskScheduler] pour la planification des rappels de quiz.
 *
 * Cette classe orchestre la planification, l'annulation et la replanification des tâches de fond (workers)
 * en se basant sur la configuration CRON des quiz. Elle interagit avec :
 * - [QuizWorkManager] : L'abstraction pour gérer les workers (ex: WorkManager sur Android).
 * - [SchedulerDataStore] : Pour persister l'état des planifications et éviter de replanifier inutilement.
 * - [CronExecutionCalculator] : Pour calculer les intervalles à partir des expressions CRON.
 *
 * @property quizWorkManager Le gestionnaire de tâches de fond.
 * @property schedulerDataStore Le magasin de données pour l'état des planifications.
 * @property cronExecutionCalculator L'utilitaire de calcul pour les expressions CRON.
 */
class CommonTaskScheduler(
    private val quizWorkManager: QuizWorkManager,
    private val schedulerDataStore: SchedulerDataStore,
    private val cronExecutionCalculator: CronExecutionCalculator,
) : TaskScheduler {

    companion object {
        const val QUIZ_REMINDER_WORK_TAG = "quiz_reminder_work"
        const val QUIZ_REMINDER_UNIQUE_WORK_PREFIX = "quiz_reminder_work_"
        private val logger = Logger.withTag(LOG_TAG)

        /**
         * Construit le nom unique du worker pour un quiz donné.
         * @param quizId L'identifiant du quiz.
         * @return Le nom unique du worker.
         */
        private fun getUniqueWorkName(quizId: String) = "$QUIZ_REMINDER_UNIQUE_WORK_PREFIX$quizId"
    }

    /**
     * Resynchronise toutes les tâches planifiées en fonction d'une liste complète de quiz.
     * Cette méthode est idéale pour une synchronisation de fond complète.
     * Elle détermine intelligemment les actions à prendre (annuler, planifier, ignorer) pour chaque quiz.
     */
    override suspend fun rescheduleAllQuizzes(quizzes: List<Quiz>) {
        logger.i { "Resynchronisation complète des rappels de quiz..." }

        try {
            val currentlyScheduledCrons = schedulerDataStore.scheduledCrons.first()
            logger.d { "Current stored crons: $currentlyScheduledCrons" }

            val quizzesById = quizzes.associateBy { it.id }
            val newCronsToStore = quizzes.mapNotNull { quiz ->
                quiz.quizCron?.let { quiz.id to (it.cron.expression to it.isEnabled) }
            }.toMap()

            // 1. Déterminer les actions pour les quiz existants et mis à jour
            val actions = currentlyScheduledCrons.map { (quizId, storedCron) ->
                val newQuiz = quizzesById[quizId]
                determineAction(quizId, newQuiz?.quizCron, storedCron)
                    .also {
                        logger.d { "Action [$it] determined for quiz $quizId"}
                    }
            }

            // 2. Déterminer les actions pour les nouveaux quiz
            val newQuizActions = quizzes
                .filter { it.id !in currentlyScheduledCrons.keys }
                .map { quiz -> determineAction(quiz.id, quiz.quizCron, null) }

            // 3. Déterminer les actions pour les quiz supprimés
            val deletedQuizActions = (currentlyScheduledCrons.keys - quizzesById.keys)
                .map { deletedQuizId ->
                    determineAction(
                        deletedQuizId,
                        null,
                        currentlyScheduledCrons[deletedQuizId]
                    )
                }

            // 4. Mettre à jour le DataStore avec la nouvelle configuration
            logger.d { "Mise à jour du DataStore avec les nouvelles configurations: $newCronsToStore" }
            schedulerDataStore.updateScheduledCrons(newCronsToStore)

            // 5. Exécuter toutes les actions déterminées
            val allActions: Map<Class<Action>, List<Action>> =
                (actions + newQuizActions + deletedQuizActions).groupBy { it.javaClass }

            allActions.get<Class<out Action>, List<Action>>(Cancel::class.java)?.let { cancelActions ->
                val idsToCancel = cancelActions.map { (it as Cancel).quizId }
                logger.i { "Annulation de ${idsToCancel.size} workers." }
                idsToCancel.forEach { cancelQuizReminder(it) }
            }

            allActions.get<Class<out Action>, List<Action>>(Enqueue::class.java)?.let { enqueueActions ->
                val quizzesToEnqueue = enqueueActions.map { it as Enqueue }
                logger.i { "Planification de ${quizzesToEnqueue.size} workers." }
                quizzesToEnqueue.forEach {
                    enqueueQuizReminder(
                        quizId = it.quizId,
                        cron = it.cron
                    )
                }
            }

            logger.i { "Resynchronisation terminée." }

        } catch (e: Exception) {
            logger.e(e) {
                "Une erreur est survenue durant rescheduleQuizReminders."
                // Selon le cas d'usage, propager l'exception peut être souhaitable
                // ou non. Ici, nous la propageons pour que l'appelant soit informé.
                throw e
            }
        }
    }

    /**
     * Planifie ou met à jour le rappel pour un seul quiz.
     * Cette méthode est plus efficace pour des mises à jour ponctuelles.
     */
    override suspend fun rescheduleQuiz(quizId: String, newCronValue: QuizCron?) {
        logger.i { "Updating quiz reminder for quiz $quizId" }
        try {
            val currentlyScheduledCrons = schedulerDataStore.scheduledCrons.first()
            val storedCron = currentlyScheduledCrons[quizId]

            val action = determineAction(
                quizId = quizId,
                newCronConfig = newCronValue,
                storedCronState = storedCron
            )
            logger.d { "Action determined for quiz $quizId: $action" }

            // Met à jour la configuration dans le DataStore
            val newCrons = currentlyScheduledCrons.toMutableMap()
            if (newCronValue != null) {
                newCrons[quizId] = newCronValue.cron.expression to newCronValue.isEnabled
            } else {
                newCrons.remove(quizId)
            }
            schedulerDataStore.updateScheduledCrons(newCrons)

            // Exécute l'action déterminée
            when (action) {
                is Cancel -> cancelQuizReminder(action.quizId)
                is Enqueue -> enqueueQuizReminder(action.quizId, action.cron)
                NoChange -> logger.d { "Aucun changement requis pour le quiz $quizId" }
            }

        } catch (e: Exception) {
            logger.e(e) { "Erreur lors de la planification pour le quiz $quizId" }
            throw e
        }
    }

    /**
     * Annule tous les rappels de quiz planifiés et nettoie le DataStore.
     */
    override suspend fun cancelAllReminders() {
        logger.i { "Tentative d'annulation de tous les rappels de quiz et de nettoyage du DataStore." }
        try {
            quizWorkManager.cancelAllWorkByTag(QUIZ_REMINDER_WORK_TAG)
            schedulerDataStore.clearAll()
            logger.i { "Tous les rappels ont été annulés et le DataStore a été vidé." }
        } catch (e: Exception) {
            logger.e(e) { "Erreur inattendue lors de cancelAllReminders." }
            // Ici, il est préférable de ne pas propager l'exception pour ne pas crasher
            // une fonctionnalité de "nettoyage".
        }
    }

    // --- Fonctions privées d'exécution ---

    /**
     * Annule le worker pour un quiz spécifique.
     * @param quizId L'ID du quiz dont le worker doit être annulé.
     */
    private fun cancelQuizReminder(quizId: String) {
        logger.d { "Annulation du worker pour le quiz '$quizId'." }
        quizWorkManager.cancelUniqueWork(getUniqueWorkName(quizId))
    }

    /**
     * Met en file d'attente un worker périodique pour un quiz.
     * @param quizId L'ID du quiz à planifier.
     * @param cron L'expression CRON pour calculer l'intervalle.
     */
    private fun enqueueQuizReminder(
        quizId: String,
        cron: CronExpression
    ) {
        val period = cronExecutionCalculator.getInterval(cron)
        if (period.isPositive()) {
            logger.d { "Planification du worker pour le quiz '$quizId' avec une période de $period." }
            quizWorkManager.enqueueUniquePeriodicWork(
                workName = getUniqueWorkName(quizId),
                initialDelaySeconds = 0, // TODO: Une logique de délai initial peut être ajoutée ici si nécessaire.
                repeatIntervalSeconds = period.inWholeSeconds,
                quizId = quizId,
                tag = QUIZ_REMINDER_WORK_TAG,
            )
        } else {
            logger.e { "L'intervalle calculé pour le quiz '$quizId' n'est pas positif ($period). La planification est annulée." }
        }
    }
}

/**
 * Classe scellée pour représenter les actions de planification possibles.
 * Rend la logique de décision plus explicite et plus sûre.
 */
private sealed class Action {
    /** Annuler le worker existant et ne rien replanifier. */
    data class Cancel(val quizId: String) : Action()

    /** Planifier (ou replanifier) un worker. */
    data class Enqueue(
        val quizId: String,
        val cron: CronExpression,
        var metadata: Map<String, Any> = emptyMap()
    ) : Action()

    /** Aucune action requise. */
    object NoChange : Action()
}

/**
 * Fonction pure qui détermine l'action à entreprendre pour un quiz donné.
 *
 * @param quizId L'ID du quiz.
 * @param newCronConfig La nouvelle configuration CRON (peut être null si le quiz est supprimé ou n'a pas de CRON).
 * @param storedCronState L'état CRON précédemment stocké (peut être null s'il s'agit d'un nouveau quiz).
 * @return L'[Action] à exécuter.
 */
private fun determineAction(
    quizId: String,
    newCronConfig: QuizCron?,
    storedCronState: Pair<String, Boolean>?
): Action {
    val newIsEnabled = newCronConfig?.isEnabled == true
    val newCronExpression = newCronConfig?.cron

    val wasEnabled = storedCronState?.second == true
    val oldCronExpressionString = storedCronState?.first

    val cronExpressionChanged = newCronExpression?.expression != oldCronExpressionString

    return when {
        // Cas 1: Le quiz a été supprimé ou son CRON a été enlevé
        newCronConfig == null -> if (storedCronState != null) Cancel(quizId) else NoChange

        // Cas 2: Le CRON est désactivé
        !newIsEnabled -> if (wasEnabled) Cancel(quizId) else NoChange

        // Cas 3: Le CRON est activé
        newIsEnabled -> when {
            storedCronState == null -> Enqueue(
                quizId,
                newCronExpression!!
            ) // Nouveau quiz activé
            cronExpressionChanged || !wasEnabled -> Enqueue(
                quizId,
                newCronExpression!!
            ) // Changement ou activation
            else -> NoChange // Rien n'a changé
        }

        else -> NoChange
    }
}
