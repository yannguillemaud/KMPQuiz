package ygmd.kmpquiz.quiz

import co.touchlab.kermit.Logger
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import ygmd.kmpquiz.createInternalQanda
import ygmd.kmpquiz.createQuizSession
import ygmd.kmpquiz.domain.usecase.QuizUseCase
import ygmd.kmpquiz.viewModel.quiz.QuizUiState
import ygmd.kmpquiz.viewModel.quiz.QuizUiState.Completed
import ygmd.kmpquiz.viewModel.quiz.QuizUiState.InProgress
import ygmd.kmpquiz.viewModel.quiz.QuizViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class QuizViewModelTest {
    private val quizUseCase: QuizUseCase = mockk()
    private val logger: Logger = mockk(relaxed = true)

    private lateinit var viewModel: QuizViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = QuizViewModel(quizUseCase, logger)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should have Idle state initially`() {
        // Given - setUp
        // Then
        assertIs<QuizUiState.Idle>(viewModel.quizUiState.value)
    }

    @Test
    fun `startQuiz should transition from Idle to InProgress on success`() = runTest {
        // Given
        val qandaIds = listOf(1L, 2L, 3L)
        val mockSession = createQuizSession(
            qandas = listOf(
                createInternalQanda(id = 1L, answers = listOf("A", "B", "C")),
                createInternalQanda(id = 2L, answers = listOf("X", "Y", "Z"))
            )
        )

        coEvery { quizUseCase.start(any()) } returns Result.success(mockSession)

        // When
        viewModel.startQuiz(qandaIds)

        // Then
        val currentState = viewModel.quizUiState.value
        assertIs<InProgress>(currentState)
        assertEquals(mockSession, currentState.session)
        assertFalse { currentState.hasAnswered }
        assertNull(currentState.selectedAnswer)
        assertEquals(setOf("A", "B", "C"), currentState.shuffledAnswers.toSet())

        coVerify { quizUseCase.start(qandaIds) }
    }

    @Test
    fun `startQuiz should transition to Error state on failure`() = runTest {
        // Given
        val qandaIds = listOf(1L, 2L)
        val errorMessage = "Network error"
        val exception = RuntimeException(errorMessage)

        coEvery { quizUseCase.start(qandaIds) } returns Result.failure(exception)

        // When
        viewModel.startQuiz(qandaIds)

        // Then
        val currentState = viewModel.quizUiState.value
        assertIs<QuizUiState.Error>(currentState)
        assertEquals(errorMessage, currentState.message)
    }

    @Test
    fun `startQuiz should shuffle answers correctly`() = runTest {
        // Given
        val qandaIds = listOf(1L)
        val qanda = createInternalQanda(
            answers = listOf("A", "B", "C", "D")
        )
        val session = createQuizSession(qandas = listOf(qanda))

        coEvery { quizUseCase.start(qandaIds) } returns Result.success(session)

        // When
        viewModel.startQuiz(qandaIds)

        // Then
        val inProgressState = viewModel.quizUiState.value as InProgress
        assertEquals(setOf("A", "B", "C", "D"), inProgressState.shuffledAnswers.toSet())
    }

    @Test
    fun `should handle qanda with different difficulties and categories`() = runTest {
        // Given
        val qandaIds = listOf(1L, 2L)
        val session = createQuizSession(
            qandas = listOf(
                createInternalQanda(category = "Science", difficulty = "Hard"),
                createInternalQanda(category = "History", difficulty = "Easy")
            )
        )
        coEvery { quizUseCase.start(qandaIds) } returns Result.success(session)

        // When
        viewModel.startQuiz(qandaIds)

        // Then
        val state = viewModel.quizUiState.value
        assertIs<InProgress>(state)
        assertContentEquals(listOf("Science", "History"), state.session.qandas.map { it.category })
    }

    @Test
    fun `should calculate results correctly with realistic questions`() = runTest {
        // Given
        val qandaIds = listOf(1L, 2L, 3L)
        val session = createQuizSession(
            qandas = listOf(
                createInternalQanda(
                    question = "What is 2+2?",
                    correctAnswer = "4",
                    answers = listOf("3", "4", "5", "6")
                ),
                createInternalQanda(
                    question = "Capital of France?",
                    correctAnswer = "Paris",
                    answers = listOf("London", "Paris", "Berlin", "Madrid")
                ),
                createInternalQanda(
                    question = "Largest planet?",
                    correctAnswer = "Jupiter",
                    answers = listOf("Earth", "Jupiter", "Mars", "Saturn")
                )
            )
        )
        coEvery { quizUseCase.start(qandaIds) } returns Result.success(session)

        // When - 2 bonnes réponses, 1 fausse
        viewModel.startQuiz(qandaIds)

        viewModel.selectAnswer("4") // Bonne
        viewModel.goToNextQuestion()

        viewModel.selectAnswer("London") // Fausse
        viewModel.goToNextQuestion()

        viewModel.selectAnswer("Jupiter") // Bonne
        viewModel.goToNextQuestion()

        // Then
        val state = viewModel.quizUiState.value
        assertIs<Completed>(state)
        assertEquals(2, state.results.score)
        assertEquals(3, state.results.questions)

        // Vérifier que les réponses sont bien enregistrées
        assertEquals(
            expected = mapOf(
                0 to "4",
                1 to "London",
                2 to "Jupiter"
            ), actual = state.session.userAnswers
        )
    }

    @Test
    fun `should handle qanda with null id`() = runTest {
        // Given
        val qandaIds = listOf(1L)
        val qandaWithNullId = createInternalQanda(id = null)
        val session = createQuizSession(qandas = listOf(qandaWithNullId))

        coEvery { quizUseCase.start(qandaIds) } returns Result.success(session)

        // When
        viewModel.startQuiz(qandaIds)

        // Then
        val state = viewModel.quizUiState.value
        assertIs<InProgress>(state)
        assertNull(state.session.qandas.first().id)
        assertNotEquals("", state.session.qandas.first().question)
    }

    @Test
    fun `should handle qanda with empty answers list`() = runTest {
        // Given
        val qandaIds = listOf(1L)
        val qandaWithEmptyAnswers = createInternalQanda(answers = emptyList())
        val session = createQuizSession(qandas = listOf(qandaWithEmptyAnswers))

        coEvery { quizUseCase.start(qandaIds) } returns Result.success(session)

        // When
        viewModel.startQuiz(qandaIds)

        // Then
        val state = viewModel.quizUiState.value
        assertIs<InProgress>(state)
        assertEquals(emptyList(), state.shuffledAnswers)
    }

    @Test
    fun `should handle qanda with single answer`() = runTest {
        // Given
        val qandaIds = listOf(1L)
        val qandaWithSingleAnswer = createInternalQanda(
            answers = listOf("OnlyAnswer"),
            correctAnswer = "OnlyAnswer"
        )
        val session = createQuizSession(qandas = listOf(qandaWithSingleAnswer))

        coEvery { quizUseCase.start(qandaIds) } returns Result.success(session)

        // When
        viewModel.startQuiz(qandaIds)

        // Then
        val state = viewModel.quizUiState.value
        assertIs<InProgress>(state)
        assertEquals(listOf("OnlyAnswer"), state.shuffledAnswers)
    }

    @Test
    fun `should maintain qanda integrity throughout quiz flow`() = runTest {
        // Given
        val originalQanda = createInternalQanda(
            id = 42L,
            category = "TestCategory",
            question = "Original Question?",
            difficulty = "Medium"
        )
        val qandaIds = listOf(42L)
        val session = createQuizSession(qandas = listOf(originalQanda))

        coEvery { quizUseCase.start(qandaIds) } returns Result.success(session)

        // When
        viewModel.startQuiz(qandaIds)
        val stateAfterStart = viewModel.quizUiState.value

        viewModel.selectAnswer("Paris")
        val stateAfterAnswer = viewModel.quizUiState.value

        viewModel.goToNextQuestion()
        val stateAfterCompletion = viewModel.quizUiState.value

        // Then - Les données de la question restent intactes
        assertIs<InProgress>(stateAfterStart)
        assertIs<InProgress>(stateAfterAnswer)
        assertIs<Completed>(stateAfterCompletion)
        assertEquals(originalQanda, stateAfterStart.session.qandas.first())
        assertEquals(originalQanda, stateAfterAnswer.session.qandas.first())
        assertEquals(originalQanda, stateAfterCompletion.session.qandas.first())
    }

    @Test
    fun `goToNextQuestion should properly handle session progression`() = runTest {
        // Given
        val qandaIds = listOf(1L, 2L, 3L)
        val session = createQuizSession(
            qandas = listOf(
                createInternalQanda(id = 1L, question = "Q1", correctAnswer = "A1"),
                createInternalQanda(id = 2L, question = "Q2", correctAnswer = "A2"),
                createInternalQanda(id = 3L, question = "Q3", correctAnswer = "A3")
            ),
            currentIndex = 0
        )
        coEvery { quizUseCase.start(qandaIds) } returns Result.success(session)

        viewModel.startQuiz(qandaIds)

        // Question 1 (index 0)
        val state = viewModel.quizUiState.value
        assertIs<InProgress>(state)
        assertEquals("Q1", state.session.currentQanda?.question)
        assertFalse { state.session.isComplete }

        viewModel.selectAnswer("Answer1")
        viewModel.goToNextQuestion()

        // Question 2 (index 1)
        val state2 = viewModel.quizUiState.value
        assertIs<InProgress>(state2)
        assertEquals("Q2", state2.session.currentQanda?.question)
        assertFalse { state2.session.isComplete }
        assertEquals(mapOf(0 to "Answer1"), state2.session.userAnswers)

        viewModel.selectAnswer("Answer2")
        viewModel.goToNextQuestion()

        // Question 3 (index 2 - dernière question)
        val state3 = viewModel.quizUiState.value
        assertIs<InProgress>(state3)
        assertEquals("Q3", state3.session.currentQanda?.question)
        assertFalse { state3.session.isComplete } // Encore une question à répondre
        assertEquals(
            mapOf(
                0 to "Answer1",
                1 to "Answer2",
            ), state3.session.userAnswers
        )

        viewModel.selectAnswer("Answer3")
        viewModel.goToNextQuestion()

        // Quiz terminé (index 3 == size)
        val completedState = viewModel.quizUiState.value
        assertIs<Completed>(completedState)
        assertTrue { completedState.session.isComplete }
        assertEquals(
            mapOf(
                0 to "Answer1",
                1 to "Answer2",
                2 to "Answer3",
            ), completedState.session.userAnswers
        )
    }

    @Test
    fun `should correctly identify quiz completion in various scenarios`() = runTest {
        // Given - Quiz avec 2 questions
        val qandaIds = listOf(1L, 2L)
        val session = createQuizSession(
            qandas = listOf(
                createInternalQanda(id = 1L),
                createInternalQanda(id = 2L)
            )
        )
        coEvery { quizUseCase.start(qandaIds) } returns Result.success(session)

        viewModel.startQuiz(qandaIds)

        // Question 1 (index 0) - pas terminé
        val state = viewModel.quizUiState.value
        assertIs<InProgress>(state)
        assertFalse { state.session.isComplete }

        viewModel.selectAnswer("Answer1")
        viewModel.goToNextQuestion()

        // Question 2 (index 1) - dernière question mais pas encore terminé
        assertIs<InProgress>(state)
        assertFalse { state.session.isComplete }

        viewModel.selectAnswer("Answer2")
        viewModel.goToNextQuestion()

        // Quiz terminé (index 2 == size)
        val completedState = viewModel.quizUiState.value
        assertIs<Completed>(completedState)
    }

    @Test
    fun `should handle single question quiz correctly`() = runTest {
        // Given - Quiz avec une seule question
        val qandaIds = listOf(1L)
        val session = createQuizSession(
            qandas = listOf(
                createInternalQanda(id = 1L, question = "Single question?")
            )
        )
        coEvery { quizUseCase.start(qandaIds) } returns Result.success(session)
        val state = viewModel.quizUiState.value
        assertIs<QuizUiState.Idle>(state)

        viewModel.startQuiz(qandaIds)

        // Question unique (index 0) - pas terminé
        val inProgressState = viewModel.quizUiState.value
        assertIs<InProgress>(inProgressState)
        assertEquals(inProgressState.session.currentQanda?.question, "Single question?")
        assertFalse { inProgressState.session.isComplete }

        viewModel.selectAnswer("Answer")
        viewModel.goToNextQuestion()

        // Quiz terminé (index 1 == size)
        assertIs<Completed>(viewModel.quizUiState.value)
    }
}