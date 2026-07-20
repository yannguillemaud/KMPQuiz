package ygmd.kmpquiz.presentation.viewModel.quiz.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random
import ygmd.kmpquiz.core.domain.qanda.AnswerContent
import ygmd.kmpquiz.core.domain.qanda.QuestionContent
import ygmd.kmpquiz.core.domain.quiz.Quiz
import ygmd.kmpquiz.core.domain.session.Session
import ygmd.kmpquiz.core.domain.session.SessionId
import ygmd.kmpquiz.core.domain.session.SessionStats
import ygmd.kmpquiz.core.usecase.category.CategoryUseCase
import ygmd.kmpquiz.core.usecase.qanda.GetQandaWithCategoryUseCase
import ygmd.kmpquiz.core.usecase.qanda.QandaWithCategory
import ygmd.kmpquiz.core.usecase.quiz.GetQuizUseCase
import ygmd.kmpquiz.core.usecase.session.ObserveSessionUseCase
import ygmd.kmpquiz.core.usecase.session.SessionResultsUseCase
import ygmd.kmpquiz.core.usecase.session.SetUpQuizSessionUseCase

sealed interface QuizSessionUiState {
    data object Loading : QuizSessionUiState
    data class Error(val message: String) : QuizSessionUiState
    data class Started(
        val title: String,
        val currentQanda: QandaUiState,
        val questionsCount: Int = 0,
        val index: Int = 0
    ) : QuizSessionUiState {
        val isLastQuestion: Boolean get() = index == questionsCount - 1
    }

    data class Idle(val title: String) : QuizSessionUiState
    data class Completed(
        val sessionId: String,
        val title: String,
        val stats: SessionStats,
        val categoriesBreakdown: List<CategoryStatUiState>,
    ) : QuizSessionUiState
}

data class QandaUiState(
    val category: String,
    val question: QuestionUiState,
    val answers: List<AnswerUiState>,
    /**
     * Id of the answer that is correct for this question. Sourced from the domain
     * ([Answers.correctAnswer]), not a positional guess, so the UI can render true
     * correct/incorrect feedback and reveal the right answer on a wrong pick.
     */
    val correctAnswerId: String,
) {
    sealed interface QuestionUiState {
        data class TextQuestionUiState(val text: String) : QuestionUiState
        data class ImageQuestionUiState(val imageUrl: String, val altText: String? = null) : QuestionUiState
    }

    data class AnswerUiState(val id: String, val text: String)
}

sealed interface QuizSessionIntent {
    data class CreateSession(val quizId: String) : QuizSessionIntent
    data class ResumeSession(val sessionId: String) : QuizSessionIntent
    data object StartSession : QuizSessionIntent
    data class SubmitAnswer(val answerId: String) : QuizSessionIntent
    data object NextState: QuizSessionIntent
}

class QuizSessionViewModel(
    getDetailedQandaUC: GetQandaWithCategoryUseCase,
    private val getQuizUC: GetQuizUseCase,
    private val setUpSessionUC: SetUpQuizSessionUseCase,
    private val observeSessionUC: ObserveSessionUseCase,
    private val submitAnswer: SubmitAnswerUseCase,
    private val nextState: NextStateSessionUseCase,
    private val sessionResultsUseCase: SessionResultsUseCase,
    private val categoryUseCase: CategoryUseCase,
) : ViewModel() {

    private val _quiz = MutableStateFlow<Quiz?>(null)

    private val _session = MutableStateFlow<Session?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val sessionState = combine(_quiz, _session) { quiz, session -> quiz to session }
        .flatMapLatest { (quiz, session) ->
            if (quiz == null || session == null) return@flatMapLatest flowOf(QuizSessionUiState.Loading)
            when (session.state) {
                Session.SessionState.Completed -> flow<QuizSessionUiState> {
                    val results = sessionResultsUseCase(session)
                    val categoriesBreakdown =
                        results.stats.categoriesStats.toCategoryStatUiStates(categoryUseCase)
                    emit(
                        QuizSessionUiState.Completed(
                            session.sessionId.id,
                            quiz.title,
                            results.stats,
                            categoriesBreakdown,
                        )
                    )
                }.catch { emit(QuizSessionUiState.Error(it.message ?: "Failed to load results")) }

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
                    imageUrl = currentQuestionInfos.qanda.question.imageUrl,
                    altText = currentQuestionInfos.qanda.question.altText
                )

                is QuestionContent.TextContent -> QandaUiState.QuestionUiState.TextQuestionUiState(
                    currentQuestionInfos.qanda.question.text
                )
            },
            answers = currentQuestionInfos.qanda.answers.answerContents.map {
                QandaUiState.AnswerUiState(
                    id = it.id,
                    text = when (it) {
                        is AnswerContent.ImageAnswerContent -> it.imageUrl
                        is AnswerContent.TextAnswerContent -> it.text
                    }
                )
            }
                // Stable shuffle seeded by the qanda id so the correct answer is not
                // always first, yet the order stays fixed across recompositions.
                .shuffled(Random(currentQuestionInfos.qanda.id.hashCode())),
            correctAnswerId = currentQuestionInfos.qanda.answers.correctAnswer.id,
        )
    )

    fun processIntent(intent: QuizSessionIntent) {
        when (intent) {
            is QuizSessionIntent.CreateSession -> {
                if (_session.value == null) {
                    viewModelScope.launch {
                        val session = createSession(intent.quizId)
                        observeSession(session)
                    }
                }
            }

            is QuizSessionIntent.ResumeSession -> {
                if (_session.value == null) {
                    viewModelScope.launch {
                        resumeSession(intent.sessionId)
                    }
                }
            }

            is QuizSessionIntent.StartSession -> {
                viewModelScope.launch {

                }
            }

            is QuizSessionIntent.SubmitAnswer -> {
                viewModelScope.launch {
                    submitAnswer(intent.answerId)
                }
            }

            is QuizSessionIntent.NextState -> {
                viewModelScope.launch {
                    nextState()
                }
            }
        }
    }

    private suspend fun observeSession(session: SessionId) {
        observeSessionUC.observeSession(session.id).collect { session ->
            if(session == null) return@collect
            if(_quiz.value == null) _quiz.value = getQuizUC.getById(session.quizId.id)
            _session.value = session
        }
    }

    private suspend fun createSession(quizId: String): SessionId {
        return setUpSessionUC.createSession(quizId).getOrThrow()
    }

    private suspend fun resumeSession(sessionId: String) {
        observeSessionUC.observeSession(sessionId).collect { session ->
            _session.value = session
        }
    }

    private suspend fun nextState() {
        nextState(_session.value)
    }

    private suspend fun submitAnswer(answerId: String) {
        submitAnswer(_session.value, answerId)
    }
}