package ygmd.kmpquiz.scheduler

import io.kotest.assertions.throwables.shouldThrow
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okio.IOException
import ygmd.kmpquiz.domain.model.cron.CronExpression
import ygmd.kmpquiz.domain.model.cron.QuizCron
import ygmd.kmpquiz.domain.model.quiz.Quiz
import ygmd.kmpquiz.domain.scheduler.QuizWorkManager
import ygmd.kmpquiz.domain.repository.SchedulerDataStore
import ygmd.kmpquiz.domain.service.CronExecutionCalculator
import ygmd.kmpquiz.infra.scheduler.CommonTaskScheduler
import ygmd.kmpquiz.infra.scheduler.QUIZ_REMINDER_UNIQUE_WORK_PREFIX_COMMON
import ygmd.kmpquiz.infra.scheduler.QUIZ_REMINDER_WORK_TAG_COMMON
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

private const val TEST_SCHEDULER_WORK_TAG = QUIZ_REMINDER_WORK_TAG_COMMON
private const val TEST_SCHEDULER_UNIQUE_PREFIX = QUIZ_REMINDER_UNIQUE_WORK_PREFIX_COMMON

class CommonAndroidTaskSchedulerTest {

    private lateinit var mockQuizWorkManager: QuizWorkManager
    private lateinit var mockSchedulerDataStore: SchedulerDataStore
    private lateinit var mockCronCalculator: CronExecutionCalculator
    private lateinit var scheduler: CommonTaskScheduler

    private val dataStoreUpdateSlot = slot<Map<String, Pair<String, Boolean>>>()

    @BeforeTest
    fun setUp() {
        mockQuizWorkManager = mockk(relaxed = true)
        mockSchedulerDataStore =
            mockk() // Pas `relaxed = true` pour être explicite sur les mocks de `data` et `updateData`
        mockCronCalculator = mockk()

        // Configuration par défaut pour mockSchedulerDataStore
        coEvery { mockSchedulerDataStore.scheduledCrons } returns flowOf(emptyMap()) // DataStore initialement vide
        coEvery { mockSchedulerDataStore.updateScheduledCrons(capture(dataStoreUpdateSlot)) } just Runs // Capture la map
        coEvery { mockSchedulerDataStore.clearAll() } just Runs

        // Configuration par défaut pour mockCronCalculator
        every { mockCronCalculator.getInterval(any()) } returns 1.hours // Un intervalle valide

        scheduler = CommonTaskScheduler(
            quizWorkManager = mockQuizWorkManager,
            schedulerDataStore = mockSchedulerDataStore,
            cronExecutionCalculator = mockCronCalculator,
        )
    }

    private fun createQuiz(id: String, cronExpressionString: String?, isEnabled: Boolean): Quiz {
        val cronConfig = if (cronExpressionString != null) {
            QuizCron(CronExpression(cronExpressionString, displayName = ""), isEnabled)
        } else {
            null
        }
        return Quiz(
            id = id,
            title = "",
            qandas = emptyList(),
            quizCron = cronConfig
        )
    }

    @Test
    fun `rescheduleQuizReminders - no existing crons, new enabled quiz - should enqueue worker and save to DataStore`() = runTest {
        val quiz1 = createQuiz("id1", "0 0 * * *", true)
        val quizzes = listOf(quiz1)

        // Assurer que le DataStore est vide au début de ce test spécifique
        coEvery { mockSchedulerDataStore.scheduledCrons } returns flowOf(emptyMap())

        scheduler.rescheduleQuizReminders(quizzes)

        coVerify { mockSchedulerDataStore.updateScheduledCrons(any()) }
        val capturedCronsMap = dataStoreUpdateSlot.captured
        assertEquals(1, capturedCronsMap.size)
        assertEquals(Pair("0 0 * * *", true), capturedCronsMap["id1"])

        verify {
            mockQuizWorkManager.enqueueUniquePeriodicWork(
                workName = "$TEST_SCHEDULER_UNIQUE_PREFIX${"id1"}",
                initialDelaySeconds = 0L, // Ou la valeur que votre logique passe
                repeatIntervalSeconds = 1.hours.inWholeSeconds,
                quizId = "id1",
                tag = TEST_SCHEDULER_WORK_TAG
            )
        }
        verify(exactly = 0) { mockQuizWorkManager.cancelUniqueWork(any()) }
    }

    @Test
    fun `rescheduleQuizReminders - no existing crons, new disabled quiz - should save to DataStore, no worker enqueued`() = runTest {
        val quiz1 = createQuiz("id1", "0 0 * * *", false)
        val quizzes = listOf(quiz1)

        coEvery { mockSchedulerDataStore.scheduledCrons } returns flowOf(emptyMap())

        scheduler.rescheduleQuizReminders(quizzes)

        coVerify { mockSchedulerDataStore.updateScheduledCrons(any()) }
        val capturedCronsMap = dataStoreUpdateSlot.captured
        assertEquals(1, capturedCronsMap.size)
        assertEquals(Pair("0 0 * * *", false), capturedCronsMap["id1"])

        verify(exactly = 0) { mockQuizWorkManager.enqueueUniquePeriodicWork(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `rescheduleQuizReminders - existing enabled quiz, now disabled - should cancel worker and update DataStore`() = runTest {
        val quiz1 = createQuiz("id1", "0 0 * * *", false) // Devient désactivé
        val quizzes = listOf(quiz1)

        val initialStoredCrons = mapOf("id1" to Pair("0 0 * * *", true))
        coEvery { mockSchedulerDataStore.scheduledCrons } returns flowOf(initialStoredCrons)

        scheduler.rescheduleQuizReminders(quizzes)

        coVerify { mockSchedulerDataStore.updateScheduledCrons(any()) }
        val capturedCrons = dataStoreUpdateSlot.captured
        assertEquals(1, capturedCrons.size)
        assertEquals(Pair("0 0 * * *", false), capturedCrons["id1"]) // Maintenant désactivé

        verify { mockQuizWorkManager.cancelUniqueWork("$TEST_SCHEDULER_UNIQUE_PREFIX${"id1"}") }
        verify(exactly = 0) { mockQuizWorkManager.enqueueUniquePeriodicWork(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `rescheduleQuizReminders - existing disabled quiz, now enabled - should enqueue worker and update DataStore`() = runTest {
        val quiz1 = createQuiz("id1", "0 0 * * *", true) // Devient activé
        val quizzes = listOf(quiz1)

        val initialStoredCrons = mapOf("id1" to Pair("0 0 * * *", false))
        coEvery { mockSchedulerDataStore.scheduledCrons } returns flowOf(initialStoredCrons)

        scheduler.rescheduleQuizReminders(quizzes)

        coVerify { mockSchedulerDataStore.updateScheduledCrons(any()) }
        val capturedCrons = dataStoreUpdateSlot.captured
        assertEquals(1, capturedCrons.size)
        assertEquals(Pair("0 0 * * *", true), capturedCrons["id1"])

        verify { mockQuizWorkManager.cancelUniqueWork("$TEST_SCHEDULER_UNIQUE_PREFIX${"id1"}") }
        verify {
            mockQuizWorkManager.enqueueUniquePeriodicWork(
                workName = "$TEST_SCHEDULER_UNIQUE_PREFIX${"id1"}",
                initialDelaySeconds = 0L,
                repeatIntervalSeconds = 1.hours.inWholeSeconds,
                quizId = "id1",
                tag = TEST_SCHEDULER_WORK_TAG
            )
        }
    }

    @Test
    fun `rescheduleQuizReminders - existing enabled quiz, cron changed - should cancel, enqueue new, update DataStore`() = runTest {
        val quiz1 = createQuiz("id1", "0 1 * * *", true) // Nouveau cron
        val quizzes = listOf(quiz1)

        val initialStoredCrons = mapOf("id1" to Pair("0 0 * * *", true)) // Ancien cron
        coEvery { mockSchedulerDataStore.scheduledCrons } returns flowOf(initialStoredCrons)
        // S'assurer que le calcul d'intervalle pour le nouveau cron est aussi mocké si différent
        every { mockCronCalculator.getInterval(CronExpression("0 1 * * *", "")) } returns 1.hours // ou une autre valeur

        scheduler.rescheduleQuizReminders(quizzes)

        coVerify { mockSchedulerDataStore.updateScheduledCrons(any()) }
        val capturedCrons = dataStoreUpdateSlot.captured
        assertEquals(1, capturedCrons.size)
        assertEquals(Pair("0 1 * * *", true), capturedCrons["id1"])

        verify { mockQuizWorkManager.cancelUniqueWork("$TEST_SCHEDULER_UNIQUE_PREFIX${"id1"}") }
        verify {
            mockQuizWorkManager.enqueueUniquePeriodicWork(
                workName = "$TEST_SCHEDULER_UNIQUE_PREFIX${"id1"}",
                initialDelaySeconds = 0L,
                repeatIntervalSeconds = 1.hours.inWholeSeconds, // Assumant 1h pour ce test
                quizId = "id1",
                tag = TEST_SCHEDULER_WORK_TAG
            )
        }
    }

    @Test
    fun `rescheduleQuizReminders - quiz removed - should cancel worker and remove from DataStore`() = runTest {
        val quizzes = emptyList<Quiz>()

        val initialStoredCrons = mapOf("id1" to Pair("0 0 * * *", true))
        coEvery { mockSchedulerDataStore.scheduledCrons } returns flowOf(initialStoredCrons)

        scheduler.rescheduleQuizReminders(quizzes)

        coVerify { mockSchedulerDataStore.updateScheduledCrons(any()) }
        val capturedCrons = dataStoreUpdateSlot.captured
        assertTrue(capturedCrons.isEmpty())

        verify { mockQuizWorkManager.cancelUniqueWork("$TEST_SCHEDULER_UNIQUE_PREFIX${"id1"}") }
        verify(exactly = 0) { mockQuizWorkManager.enqueueUniquePeriodicWork(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `rescheduleQuizReminders - multiple changes - complex scenario`() = runTest {
        val quizEnabledToDisabled = createQuiz("q1", "0 0 * * *", false)
        val quizDisabledToEnabled = createQuiz("q2", "0 1 * * *", true)
        val quizNewEnabled = createQuiz("q3", "0 2 * * *", true)
        val quizCronChanged = createQuiz("q4", "0 4 * * *", true) // était "0 3 * * *"
        // quiz q5 sera supprimé

        val quizzes = listOf(quizEnabledToDisabled, quizDisabledToEnabled, quizNewEnabled, quizCronChanged)

        val initialStoredCrons = mapOf(
            "q1" to Pair("0 0 * * *", true),
            "q2" to Pair("0 1 * * *", false),
            // q3 est nouveau, donc pas dans initialStoredCrons
            "q4" to Pair("0 3 * * *", true),
            "q5" to Pair("0 5 * * *", true)
        )
        coEvery { mockSchedulerDataStore.scheduledCrons } returns flowOf(initialStoredCrons)
        // Mocker les calculs d'intervalle pour tous les crons actifs pertinents
        every { mockCronCalculator.getInterval(CronExpression("0 1 * * *", "")) } returns 1.hours
        every { mockCronCalculator.getInterval(CronExpression("0 2 * * *", "")) } returns 1.hours
        every { mockCronCalculator.getInterval(CronExpression("0 4 * * *", "")) } returns 1.hours


        scheduler.rescheduleQuizReminders(quizzes)

        coVerify { mockSchedulerDataStore.updateScheduledCrons(any()) }
        val captured = dataStoreUpdateSlot.captured
        assertEquals(4, captured.size)
        assertEquals(Pair("0 0 * * *", false), captured["q1"])
        assertEquals(Pair("0 1 * * *", true), captured["q2"])
        assertEquals(Pair("0 2 * * *", true), captured["q3"])
        assertEquals(Pair("0 4 * * *", true), captured["q4"])

        // Annulations
        verify { mockQuizWorkManager.cancelUniqueWork("$TEST_SCHEDULER_UNIQUE_PREFIX${"q1"}") }
        verify { mockQuizWorkManager.cancelUniqueWork("$TEST_SCHEDULER_UNIQUE_PREFIX${"q2"}") }
        verify { mockQuizWorkManager.cancelUniqueWork("$TEST_SCHEDULER_UNIQUE_PREFIX${"q4"}") }
        verify { mockQuizWorkManager.cancelUniqueWork("$TEST_SCHEDULER_UNIQUE_PREFIX${"q5"}") }

        // Planifications (enqueue)
        verify(exactly = 0) { mockQuizWorkManager.enqueueUniquePeriodicWork(workName = "$TEST_SCHEDULER_UNIQUE_PREFIX${"q1"}", any(), any(), any(), any()) } // q1 désactivé
        verify { mockQuizWorkManager.enqueueUniquePeriodicWork(workName = "$TEST_SCHEDULER_UNIQUE_PREFIX${"q2"}", initialDelaySeconds = 0L, repeatIntervalSeconds = 1.hours.inWholeSeconds, quizId = "q2", tag = TEST_SCHEDULER_WORK_TAG) }
        verify { mockQuizWorkManager.enqueueUniquePeriodicWork(workName = "$TEST_SCHEDULER_UNIQUE_PREFIX${"q3"}", initialDelaySeconds = 0L, repeatIntervalSeconds = 1.hours.inWholeSeconds, quizId = "q3", tag = TEST_SCHEDULER_WORK_TAG) }
        verify { mockQuizWorkManager.enqueueUniquePeriodicWork(workName = "$TEST_SCHEDULER_UNIQUE_PREFIX${"q4"}", initialDelaySeconds = 0L, repeatIntervalSeconds = 1.hours.inWholeSeconds, quizId = "q4", tag = TEST_SCHEDULER_WORK_TAG) }
    }

    @Test
    fun `cancelAllReminders - should call cancelAllWorkByTag and clear DataStore`() = runTest {
        scheduler.cancelAllReminders()

        verify { mockQuizWorkManager.cancelAllWorkByTag(TEST_SCHEDULER_WORK_TAG) }
        coVerify { mockSchedulerDataStore.clearAll() }
    }

    @Test
    fun `rescheduleQuizReminders - cronExecutionCalculator returns zero period - should not enqueue but save config`() = runTest {
        val quiz1 = createQuiz("id1", "invalid-cron", true)
        val quizzes = listOf(quiz1)

        every { mockCronCalculator.getInterval(CronExpression("invalid-cron", "")) } returns Duration.ZERO // Période invalide/nulle

        coEvery { mockSchedulerDataStore.scheduledCrons } returns flowOf(emptyMap())

        scheduler.rescheduleQuizReminders(quizzes)

        coVerify { mockSchedulerDataStore.updateScheduledCrons(any()) } // Doit toujours sauvegarder la config
        val capturedCronsMap = dataStoreUpdateSlot.captured
        assertEquals(1, capturedCronsMap.size)
        assertEquals(Pair("invalid-cron", true), capturedCronsMap["id1"])

        verify(exactly = 0) { mockQuizWorkManager.enqueueUniquePeriodicWork(any(), any(), any(), any(), any()) } // Aucune planification
    }

    @Test
    fun `rescheduleQuizReminders - DataStore read error - should log error and not proceed catastrophically`() = runTest {
        val quiz1 = createQuiz("id1", "0 0 * * *", true)
        val quizzes = listOf(quiz1)
        val ioException = IOException("Test DataStore read error")

        coEvery { mockSchedulerDataStore.scheduledCrons } throws ioException // Simuler une erreur de lecture

        shouldThrow<IOException> { scheduler.rescheduleQuizReminders(quizzes) }

        // Ne devrait pas essayer de mettre à jour le DataStore ou d'interagir avec WorkManager si la lecture initiale échoue de cette manière
        coVerify(exactly = 0) { mockSchedulerDataStore.updateScheduledCrons(any()) }
        verify(exactly = 0) { mockQuizWorkManager.enqueueUniquePeriodicWork(any(), any(), any(), any(), any()) }
        verify(exactly = 0) { mockQuizWorkManager.cancelUniqueWork(any()) }
    }
}
