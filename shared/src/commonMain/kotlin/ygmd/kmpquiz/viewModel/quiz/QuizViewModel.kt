package ygmd.kmpquiz.viewModel.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.entities.qanda.AnswerContent
import ygmd.kmpquiz.domain.entities.quiz.QuizSession
import ygmd.kmpquiz.domain.usecase.QuizUseCase
import ygmd.kmpquiz.viewModel.quiz.QuizUiState.Idle
import ygmd.kmpquiz.viewModel.quiz.QuizUiState.InProgress

class QuizViewModel(
    private val quizUseCase: QuizUseCase,
    private val logger: Logger,
) : ViewModel() {
    private val _uiState = MutableStateFlow<QuizUiState>(Idle)
    val quizUiState = _uiState.asStateFlow()

    init {
        _uiState.value = Idle
    }

    fun startQuiz(qandaIds: List<Long>) {
        viewModelScope.launch {
            quizUseCase.start(qandaIds).fold(
                onSuccess = { session ->
                    logger.i { "Quiz started with ${session.qandas.size} questions" }
                    _uiState.value = InProgress(
                        session = session,
                        shuffledAnswers = session.currentQanda?.answers?.shuffled() ?: emptyList()
                    )
                },
                onFailure = { error ->
                    val errorMessage = "Failed to start quiz: ${error.message}"
                    logger.e { errorMessage }
                    _uiState.value = QuizUiState.Error(error.message ?: errorMessage)
                }
            )
        }
    }

    fun selectAnswer(answer: AnswerContent) {
        val currentState = _uiState.value
        if (currentState is InProgress && !currentState.hasAnswered) {
            _uiState.value = currentState.copy(
                hasAnswered = true,
                selectedAnswer = answer
            )
            logger.d { "Answer selected: $answer" }
        }
    }

    fun goToNextQuestion() {
        val currentState = _uiState.value
        if (currentState !is InProgress) {
            logger.w { "Cannot go to next question in current state" }
            return
        }

        val session = currentState.session
        val selectedAnswer = currentState.selectedAnswer ?: return

        val updatedSession = session.copy(
            userAnswers = session.userAnswers + (session.currentIndex to selectedAnswer),
            currentIndex = session.currentIndex +1,
        )

        if (updatedSession.isComplete) {
            // Quiz terminé
            val results = calculateResults(updatedSession)
            _uiState.value = QuizUiState.Completed(
                session = updatedSession,
                results = results
            )
            logger.i { "Quiz completed with score: ${results.score}/${results.questions}" }
        } else {
            // Question suivante
            _uiState.value = InProgress(
                session = updatedSession,
                shuffledAnswers = updatedSession.currentQanda?.answers?.shuffled() ?: emptyList(),
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