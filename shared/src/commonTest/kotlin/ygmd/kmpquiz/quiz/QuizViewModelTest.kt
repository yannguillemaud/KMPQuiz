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
import org.assertj.core.api.Assertions.assertThat
import ygmd.kmpquiz.createInternalQanda
import ygmd.kmpquiz.createQuizSession
import ygmd.kmpquiz.domain.usecase.QuizUseCase
import ygmd.kmpquiz.viewModel.quiz.QuizUiState
import ygmd.kmpquiz.viewModel.quiz.QuizUiState.InProgress
import ygmd.kmpquiz.viewModel.quiz.QuizViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs

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
    fun `should have Idle state initially`(){
        // Given - setUp
        // Then
        assertThat(viewModel.quizUiState.value)
            .isInstanceOf(QuizUiState.Idle::class.java)
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
        assertThat(currentState)
            .isInstanceOf(InProgress::class.java)

        val inProgressState = currentState as InProgress
        assertThat(inProgressState.session).isEqualTo(mockSession)
        assertThat(inProgressState.hasAnswered).isFalse
        assertThat(inProgressState.selectedAnswer).isNull()
        assertThat(inProgressState.shuffledAnswers).containsExactlyInAnyOrder(
            "A", "B", "C"
        )

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
        assertThat(currentState).isInstanceOf(QuizUiState.Error::class.java)

        val errorState = currentState as QuizUiState.Error
        assertThat(errorState.message).isEqualTo(errorMessage)
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
        assertThat(inProgressState.shuffledAnswers)
            .hasSize(4)
            .containsExactlyInAnyOrder("A", "B", "C", "D")
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
        val inProgressState = viewModel.quizUiState.value as InProgress
        assertThat(inProgressState.session.qandas).hasSize(2)
        assertThat(inProgressState.session.qandas[0].category).isEqualTo("Science")
        assertThat(inProgressState.session.qandas[1].category).isEqualTo("History")
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
        val completedState = viewModel.quizUiState.value as QuizUiState.Completed
        assertThat(completedState.results.score).isEqualTo(2)
        assertThat(completedState.results.questions).isEqualTo(3)

        // Vérifier que les réponses sont bien enregistrées
        assertThat(completedState.session.userAnswers)
            .containsEntry(0, "4")
            .containsEntry(1, "London")
            .containsEntry(2, "Jupiter")
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
        val inProgressState = viewModel.quizUiState.value as InProgress
        assertThat(inProgressState.session.qandas.first().id).isNull()
        assertThat(inProgressState.session.qandas.first().question).isNotEmpty()
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
        val inProgressState = viewModel.quizUiState.value as InProgress
        assertThat(inProgressState.shuffledAnswers).isEmpty()
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
        val inProgressState = viewModel.quizUiState.value as InProgress
        assertThat(inProgressState.shuffledAnswers).containsExactly("OnlyAnswer")
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
        val stateAfterStart = viewModel.quizUiState.value as InProgress

        viewModel.selectAnswer("Paris")
        val stateAfterAnswer = viewModel.quizUiState.value as InProgress

        viewModel.goToNextQuestion()
        val stateAfterCompletion = viewModel.quizUiState.value as QuizUiState.Completed

        // Then - Les données de la question restent intactes
        assertThat(stateAfterStart.session.qandas.first()).isEqualTo(originalQanda)
        assertThat(stateAfterAnswer.session.qandas.first()).isEqualTo(originalQanda)
        assertThat(stateAfterCompletion.session.qandas.first()).isEqualTo(originalQanda)
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
        assertThat((viewModel.quizUiState.value as InProgress).session.currentQanda?.question).isEqualTo("Q1")
        assertThat((viewModel.quizUiState.value as InProgress).session.isComplete).isFalse()

        viewModel.selectAnswer("Answer1")
        viewModel.goToNextQuestion()

        // Question 2 (index 1)
        val state2 = viewModel.quizUiState.value as InProgress
        assertThat(state2.session.currentQanda?.question).isEqualTo("Q2")
        assertThat(state2.session.isComplete).isFalse()
        assertThat(state2.session.userAnswers).containsEntry(0, "Answer1")

        viewModel.selectAnswer("Answer2")
        viewModel.goToNextQuestion()

        // Question 3 (index 2 - dernière question)
        val state3 = viewModel.quizUiState.value as InProgress
        assertThat(state3.session.currentQanda?.question).isEqualTo("Q3")
        assertThat(state3.session.isComplete).isFalse() // Encore une question à répondre
        assertThat(state3.session.userAnswers)
            .containsEntry(0, "Answer1")
            .containsEntry(1, "Answer2")

        viewModel.selectAnswer("Answer3")
        viewModel.goToNextQuestion()

        // Quiz terminé (index 3 == size)
        val completedState = viewModel.quizUiState.value as QuizUiState.Completed
        assertThat(completedState.session.isComplete).isTrue()
        assertThat(completedState.session.userAnswers)
            .containsEntry(0, "Answer1")
            .containsEntry(1, "Answer2")
            .containsEntry(2, "Answer3")
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
        assertThat((viewModel.quizUiState.value as InProgress).session.isComplete).isFalse()

        viewModel.selectAnswer("Answer1")
        viewModel.goToNextQuestion()

        // Question 2 (index 1) - dernière question mais pas encore terminé
        assertThat((viewModel.quizUiState.value as InProgress).session.isComplete).isFalse()

        viewModel.selectAnswer("Answer2")
        viewModel.goToNextQuestion()

        // Quiz terminé (index 2 == size)
        assertThat(viewModel.quizUiState.value).isInstanceOf(QuizUiState.Completed::class.java)
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
        assertIs<QuizUiState.Completed>(viewModel.quizUiState.value)
    }
}