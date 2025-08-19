package ygmd.kmpquiz.viewModel.quiz.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ygmd.kmpquiz.application.usecase.quiz.GetQuizUseCase
import ygmd.kmpquiz.application.usecase.quiz.StartQuizSessionUseCase
import ygmd.kmpquiz.domain.entities.qanda.AnswerSet.AnswerContent
import ygmd.kmpquiz.domain.entities.quiz.Quiz
import ygmd.kmpquiz.domain.entities.quiz.QuizSession
import ygmd.kmpquiz.viewModel.quiz.session.QuizSessionUiState.Idle
import ygmd.kmpquiz.viewModel.quiz.session.QuizSessionUiState.InProgress

sealed interface QuizSessionIntent {
    data class StartQuizSession(val quizId: String) : QuizSessionIntent
    data class SelectAnswer(val answer: AnswerContent) : QuizSessionIntent
    data object NextQuestion : QuizSessionIntent
}

private val logger = Logger.withTag("QuizViewModel")

class QuizSessionViewModel(
    private val getQuizUseCase: GetQuizUseCase,
    private val startQuizSessionUseCase: StartQuizSessionUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<QuizSessionUiState>(Idle)
    val quizUiState = _uiState.asStateFlow()

    // TODO - init { start/continue quiz depending on current state } ?

    fun processIntent(quizSessionIntent: QuizSessionIntent) {
        when (quizSessionIntent) {
            is QuizSessionIntent.StartQuizSession -> { startQuiz(quizSessionIntent.quizId) }
            is QuizSessionIntent.NextQuestion -> goToNextQuestion()
            is QuizSessionIntent.SelectAnswer -> selectAnswer(quizSessionIntent.answer)
        }
    }

    private fun startQuiz(quizId: String) {
        viewModelScope.launch {
            val quiz = getQuizUseCase.getQuizById(quizId) ?: run {
                _uiState.value = QuizSessionUiState.Error("Quiz not found")
                return@launch
            }

            val quizSession = quiz.session()
            startQuizSessionUseCase.start(quizSession).fold(
                onSuccess = { session ->
                    _uiState.value = InProgress(
                        session = session,
                        shuffledAnswers = session.currentQanda?.answers?.shuffled()
                    )
                },
                onFailure = { error ->
                    val message = "Failed to start quiz: ${error.message}"
                    logger.e(error) { message }
                    _uiState.value = QuizSessionUiState.Error(error.message ?: message)
                }
            )
        }
    }

    private fun selectAnswer(answer: AnswerContent) {
        val currentState = _uiState.value
        if (currentState is InProgress && !currentState.hasAnswered) {
            _uiState.value = currentState.copy(
                hasAnswered = true,
                selectedAnswer = answer
            )
            logger.d { "Answer selected: $answer" }
        }
    }

    private fun goToNextQuestion() {
        val currentState = _uiState.value
        if (currentState !is InProgress) {
            logger.w { "Cannot go to next question in current state" }
            return
        }

        val session = currentState.session
        val selectedAnswer = currentState.selectedAnswer ?: return

        val updatedSession = session.copy(
            userAnswers = session.userAnswers + (session.currentIndex to selectedAnswer),
            currentIndex = session.currentIndex + 1,
        )

        if (updatedSession.isComplete) {
            // Quiz terminé
            val results = calculateResults(updatedSession)
            _uiState.value = QuizSessionUiState.Completed(
                session = updatedSession,
                results = results
            )
            logger.i { "Quiz completed with score: ${results.score}/${results.questions}" }
        } else {
            // Question suivante
            _uiState.value = InProgress(
                session = updatedSession,
                shuffledAnswers = updatedSession.currentQanda?.answers?.shuffled(),
                hasAnswered = false,
                selectedAnswer = null
            )
            logger.d { "Moved to question ${updatedSession.currentIndex}/${updatedSession.qandas.size}" }
        }
    }

    private fun calculateResults(session: QuizSession): QuizResult {
        val correctAnswers = session.userAnswers.count { (index, userAnswer) ->
            session.qandas.getOrNull(index)?.correctAnswer == userAnswer
        }

        return QuizResult(
            questions = session.qandas.size,
            score = correctAnswers
        )
    }
}

private fun Quiz.session() = QuizSession(
    quizId = id,
    title = title,
    qandas = qandas,
)
