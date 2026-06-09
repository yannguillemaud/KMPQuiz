package ygmd.kmpquiz.presentation.viewModel.quiz.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.core.domain.qanda.AnswerContent
import ygmd.kmpquiz.core.domain.qanda.QuestionContent
import ygmd.kmpquiz.core.domain.quiz.Quiz
import ygmd.kmpquiz.core.domain.session.Session
import ygmd.kmpquiz.core.usecase.qanda.GetQandaWithCategoryUseCase
import ygmd.kmpquiz.core.usecase.qanda.QandaWithCategory
import ygmd.kmpquiz.core.usecase.quiz.GetQuizUseCase
import ygmd.kmpquiz.core.usecase.session.ObserveSessionUseCase
import ygmd.kmpquiz.core.usecase.session.SetUpQuizSessionUseCase

sealed interface QuizSessionUiState {
    data object Loading : QuizSessionUiState
    data class Error(val message: String) : QuizSessionUiState
    data class Started(
        val title: String,
        val currentQanda: QandaUiState,
        val questionsCount: Int = 0,
        val index: Int = 0
    ) : QuizSessionUiState

    data class Idle(val title: String) : QuizSessionUiState
    data class Completed(val sessionId: String, val title: String) : QuizSessionUiState
}

data class QandaUiState(
    val category: String,
    val question: QuestionUiState,
    val answers: List<AnswerUiState>,
) {
    sealed interface QuestionUiState {
        data class TextQuestionUiState(val text: String) : QuestionUiState
        data class ImageQuestionUiState(val imageUrl: String) : QuestionUiState
    }

    data class AnswerUiState(val id: String, val text: String)
}

sealed interface QuizSessionIntent {
    data object CreateSession : QuizSessionIntent
    data class ResumeSession(val sessionId: String) : QuizSessionIntent
    data object StartSession : QuizSessionIntent
    data class SubmitAnswer(val answerId: String) : QuizSessionIntent
}

class QuizSessionViewModel(
    savedStateHandle: SavedStateHandle,
    getDetailedQandaUC: GetQandaWithCategoryUseCase,
    private val getQuizUC: GetQuizUseCase,
    private val setUpSessionUC: SetUpQuizSessionUseCase,
    private val observeSessionUC: ObserveSessionUseCase,
    private val submitAnswer: SubmitAnswerUseCase,
    private val nextState: NextStateSessionUseCase,
) : ViewModel() {

    private val _quizId: String? = savedStateHandle["quizId"]

    private val _quiz = MutableStateFlow<Quiz?>(null)
    private val _session = MutableStateFlow<Session?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val sessionState = combine(_quiz, _session) { quiz, session -> quiz to session }
        .flatMapLatest { (quiz, session) ->
            if (quiz == null || session == null) return@flatMapLatest flowOf(QuizSessionUiState.Loading)
            when (session.state) {
                Session.SessionState.Completed -> flowOf(
                    QuizSessionUiState.Completed(
                        session.sessionId.id,
                        quiz.title
                    )
                )

                is Session.SessionState.InProgress -> {
                    val qandaId = session.questions[session.state.index].id
                    val qandaWithCategory =
                        getDetailedQandaUC(qandaId) ?: return@flatMapLatest flowOf(
                            QuizSessionUiState.Error("Qanda not found")
                        )
                    flowOf(started(quiz, session, qandaWithCategory))
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = QuizSessionUiState.Loading
        )

    private fun started(
        quiz: Quiz,
        session: Session,
        currentQuestionInfos: QandaWithCategory
    ): QuizSessionUiState.Started = QuizSessionUiState.Started(
        title = quiz.title,
        questionsCount = session.questions.size,
        index = (session.state as Session.SessionState.InProgress).index,
        currentQanda = QandaUiState(
            category = currentQuestionInfos.category.name,
            question = when (currentQuestionInfos.qanda.question) {
                is QuestionContent.ImageContent -> QandaUiState.QuestionUiState.ImageQuestionUiState(
                    currentQuestionInfos.qanda.question.imageUrl
                )

                is QuestionContent.TextContent -> QandaUiState.QuestionUiState.TextQuestionUiState(
                    currentQuestionInfos.qanda.question.text
                )
            },
            answers = currentQuestionInfos.qanda.answers.map {
                QandaUiState.AnswerUiState(
                    id = it.id,
                    text = when (it) {
                        is AnswerContent.ImageAnswerContent -> it.imageUrl
                        is AnswerContent.TextAnswerContent -> it.text
                    }
                )
            }
        )
    )

    fun processIntent(intent: QuizSessionIntent) {
        when (intent) {
            is QuizSessionIntent.CreateSession -> {
                requireNotNull(_quizId)
                viewModelScope.launch {
                    createFreshSession(_quizId)
                    nextState()
                }
            }

            is QuizSessionIntent.ResumeSession -> {
                viewModelScope.launch {
                    resumeSession(intent.sessionId)
                }
            }

            is QuizSessionIntent.StartSession -> {
                viewModelScope.launch {
                    nextState()
                }
            }

            is QuizSessionIntent.SubmitAnswer -> {
                viewModelScope.launch {
                    submitAnswer(intent.answerId)
                    nextState()
                }
            }
        }
    }

    private suspend fun createFreshSession(quizId: String) {
        setUpSessionUC.createSession(quizId).onSuccess { sessionId ->
            observeSessionUC.observeSession(sessionId.id).collect { session ->
                _session.value = session
                _quiz.value = getQuizUC.getById(quizId)
            }
        }
    }

    private suspend fun resumeSession(sessionId: String) {
        observeSessionUC.observeSession(sessionId).collect { session ->
            _session.value = session
            _quiz.value = getQuizUC.getById(session.quizId.id)
        }
    }

    private suspend fun nextState() {
        nextState(_session.value)
    }

    private suspend fun submitAnswer(answerId: String) {
        submitAnswer(_session.value, answerId)
    }
}