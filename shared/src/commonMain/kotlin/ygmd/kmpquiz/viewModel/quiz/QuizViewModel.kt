package ygmd.kmpquiz.viewModel.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.pojo.QuizSession
import ygmd.kmpquiz.domain.repository.qanda.QandaRepository
import ygmd.kmpquiz.viewModel.quiz.QuizUiState.InProgress
import ygmd.kmpquiz.viewModel.quiz.QuizUiState.Loading

class QuizViewModel(
    private val qandaRepository: QandaRepository,
    private val logger: Logger,
) : ViewModel() {
    private val _uiState = MutableStateFlow<QuizUiState>(Loading)
    val quizUiState = _uiState.asStateFlow()

    fun startQuiz(qandaIds: List<Long>) {
        viewModelScope.launch {
            try {
                val qandas = qandaIds.mapNotNull { id ->
                    qandaRepository.findById(id).getOrNull()
                }

                if(qandas.isEmpty()){
                    _uiState.value = QuizUiState.Error("Aucun quiz trouvé")
                    return@launch
                }

                _uiState.value = InProgress(
                    session = QuizSession(qandas)
                )
                updateCurrentQuestion()
            } catch (e: Exception){
                logger.e(e){ "Erreur lors du chargement du quiz"}
                _uiState.value = QuizUiState.Error("Erreur de chargement")
            }
        }
    }

    fun selectAnswer(answer: String) {
        when (val currentState = _uiState.value) {
            is InProgress -> {
                _uiState.value = currentState.copy(
                    hasAnswered = true,
                    selectedAnswer = answer,
                )
            }

            is QuizUiState.Error -> {
                val message = (quizUiState.value as QuizUiState.Error).message
                logger.w("Error: $message")
            }

            else -> { /* Should not happen */
            }
        }
    }

    fun goToNextQuestion() {
        when (val currentState = _uiState.value) {
            is InProgress -> {
                val session = currentState.session
                val selectedAnswer = currentState.selectedAnswer
                    ?: return
                val updatedSession = session.copy(
                    userAnswers = session.userAnswers +
                            (session.currentIndex to selectedAnswer)
                )
                if (session.isLastQuestion) {
                    _uiState.value = QuizUiState.Completed(
                        session = updatedSession,
                        results = QuizResult(
                            questions = updatedSession.userAnswers.size,
                            score = calculateScore(updatedSession)
                        )
                    )
                    return
                }

                val nextSession = updatedSession.copy(
                    currentIndex = updatedSession.currentIndex + 1,
                )
                _uiState.value = InProgress(
                    session = nextSession,
                    hasAnswered = false,
                    selectedAnswer = null,
                )
            }

            is QuizUiState.Error -> {
                val message = (quizUiState.value as QuizUiState.Error).message
                logger.w("Error: $message")
            }

            else -> { /* Should not happen */
            }
        }
    }

    private fun updateCurrentQuestion() {
        when (val currentState = _uiState.value) {
            is InProgress -> {
                val currentQanda = currentState.session.currentQanda
                if (currentQanda != null) {
                    _uiState.value = currentState.copy(
                        shuffledAnswers = currentQanda.answers.shuffled()
                    )
                } else {
                    _uiState.value = QuizUiState.Error("Question introuvable")
                }
            }

            else -> {
                logger.w("updateCurrentQuestion called in invalid state: ${currentState::class.simpleName}")
            }
        }
    }

    private fun calculateScore(session: QuizSession): Int {
        var correctAnswersCount = 0
        for (userAnswer in session.userAnswers) {
            val (index, answer) = userAnswer
            if(session.qandas[index].correctAnswer == answer) correctAnswersCount++
        }
        return correctAnswersCount
    }
}