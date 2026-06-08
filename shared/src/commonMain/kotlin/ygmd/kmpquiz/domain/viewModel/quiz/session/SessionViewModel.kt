package ygmd.kmpquiz.domain.viewModel.quiz.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.model.qanda.QuestionContent.ImageContent
import ygmd.kmpquiz.domain.model.qanda.QuestionContent.TextContent
import ygmd.kmpquiz.domain.model.quiz.Quiz
import ygmd.kmpquiz.domain.model.quiz.session.QandaId
import ygmd.kmpquiz.domain.model.quiz.session.QuizSession
import ygmd.kmpquiz.domain.model.quiz.session.SessionStats
import ygmd.kmpquiz.domain.usecase.category.CategoryUseCase
import ygmd.kmpquiz.domain.usecase.qanda.GetQandaUseCase
import ygmd.kmpquiz.domain.usecase.quiz.GetQuizUseCase
import ygmd.kmpquiz.domain.usecase.quizSession.ObserveSessionUseCase
import ygmd.kmpquiz.domain.usecase.quizSession.SessionResultsUseCase
import ygmd.kmpquiz.domain.viewModel.quiz.session.QandaUiState.QuestionUiState.ImageQuestionUiState
import ygmd.kmpquiz.domain.viewModel.quiz.session.QandaUiState.QuestionUiState.TextQuestionUiState
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ofPattern

data class SessionsUiState(
    val isLoading: Boolean = false,
    val sessions: List<SessionHistoryUiState> = emptyList(),
)

data class SessionHistoryUiState(
    val sessionId: String,
    val quizId: String,
    val quizTitle: String,
    val sessionState: QuizSession.SessionState,
    val createdAt: String,
    val updatedAt: String,
) {
    val historyAvailable: Boolean get() = sessionState is QuizSession.SessionState.Completed
}

class SessionViewModel(
    private val getSessionsUseCase: ObserveSessionUseCase,
    private val getQuizUseCase: GetQuizUseCase,
) : ViewModel() {
    companion object {
        private val dateTimeFormatter: DateTimeFormatter = ofPattern("yyyy/MM/dd HH:mm")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val sessions = getSessionsUseCase.observeSummarizedSessions()
        .flatMapLatest { sessions ->
            val sessions = sessions
                .map {
                    SessionHistoryUiState(
                        sessionId = it.sessionId,
                        quizId = it.quizId,
                        quizTitle = getQuizUseCase.getById(it.quizId)?.title ?: "Unknown Quiz",
                        sessionState = it.state,
                        createdAt = dateTimeFormatter.format(it.createdAt),
                        updatedAt = dateTimeFormatter.format(it.updatedAt)
                    )
                }
            flowOf(SessionsUiState(sessions = sessions))
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SessionsUiState(isLoading = true)
        )
}

sealed interface DetailedSessionUiState {
    data object Loading : DetailedSessionUiState
    data class Error(val message: String) : DetailedSessionUiState
    data class Loaded(
        val title: String,
        val stats: SessionStats,
        val categoriesBreakdown: List<CategoryStatUiState>,
        val userAnswers: List<Pair<QandaUiState, UserAnswerState>>
    ) : DetailedSessionUiState
}

data class CategoryStatUiState(
    val categoryName: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val successRate: Float
)

data class UserAnswerState(
    val userAnswerContent: QandaUiState.AnswerUiState,
    val correctAnswerContent: QandaUiState.AnswerUiState,
) {
    val isCorrect: Boolean = userAnswerContent.id == correctAnswerContent.id
}

class DetailedSessionViewModel(
    private val getSessionsUseCase: ObserveSessionUseCase,
    private val getQuizUseCase: GetQuizUseCase,
    private val sessionResultsUseCase: SessionResultsUseCase,
    private val getQandaUseCase: GetQandaUseCase,
    private val categoryUseCase: CategoryUseCase,
) : ViewModel() {
    private val _quiz = MutableStateFlow<Quiz?>(null)
    private val _session = MutableStateFlow<QuizSession?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val sessionState = combine(_quiz, _session) { quiz, session -> quiz to session }
        .flatMapLatest { (quiz, session) ->
            when {
                quiz == null -> flowOf(DetailedSessionUiState.Error("Quiz not found"))
                session == null -> flowOf(DetailedSessionUiState.Loading)
                else -> flow(sessionResults(session, quiz))
            }.catch { DetailedSessionUiState.Error(it.message ?: "Unknown error") }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DetailedSessionUiState.Loading
        )

    private fun sessionResults(
        session: QuizSession,
        quiz: Quiz
    ): suspend FlowCollector<DetailedSessionUiState>.() -> Unit = {
        val results = sessionResultsUseCase(session)
        val userAnswers = results.userResults
            .map { (qandaId, userAnswer) ->
                val qanda =
                    getQandaUseCase.getById(qandaId.id) ?: error("Qanda ${qandaId.id} not found.")
                val qandaState = qanda.let { qanda ->
                    QandaUiState(
                        category = categoryUseCase.getById(qanda.categoryId)?.name
                            ?: error("Category ${qanda.categoryId} not found"),
                        question = when (qanda.question) {
                            is ImageContent -> ImageQuestionUiState(qanda.question.imageUrl)
                            is TextContent -> TextQuestionUiState(qanda.question.text)
                        },
                        answers = qanda.answers.map {
                            QandaUiState.AnswerUiState(it.id, it.contextKey)
                        }
                    )
                }
                val answer = UserAnswerState(
                    userAnswerContent = qanda.answers.first { it.id == userAnswer.answerId.id }
                        .let {
                            QandaUiState.AnswerUiState(it.id, it.contextKey)
                        },
                    correctAnswerContent = qanda.answers.correctAnswer.let {
                        QandaUiState.AnswerUiState(it.id, it.contextKey)
                    }
                )
                qandaState to answer
            }

        val categoriesBreakdown = results.stats.categoriesStats.map { stat ->
            CategoryStatUiState(
                categoryName = categoryUseCase.getById(stat.categoryId)?.name
                    ?: "Unknown",
                totalQuestions = stat.totalQuestions,
                correctAnswers = stat.correctAnswers,
                successRate = stat.successRate
            )
        }

        emit(
            DetailedSessionUiState.Loaded(
                title = quiz.title,
                stats = results.stats,
                categoriesBreakdown = categoriesBreakdown,
                userAnswers = userAnswers
            )
        )
    }

    fun initialize(sessionId: String) {
        if (_session.value != null) return
        viewModelScope.launch {
            getSessionsUseCase.observeSession(sessionId).collect {
                _session.value = it
                _quiz.value = getQuizUseCase.getById(it.quizId.id)
            }
        }
    }
}
