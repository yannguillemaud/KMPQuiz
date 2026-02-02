package ygmd.kmpquiz.domain.usecase.quizSession

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ygmd.kmpquiz.domain.model.qanda.Choice
import ygmd.kmpquiz.domain.model.quiz.Quiz
import ygmd.kmpquiz.domain.model.quiz.QuizSession

class QuizSessionUseCase(
    private val quizSessionRepository: QuizSessionRepository
) {
    fun observeSession(): Flow<QuizSession?> = quizSessionRepository.observeSession()

    suspend fun initSession(quiz: Quiz) {
        quizSessionRepository.initSession(quiz)
    }

    suspend fun nextState() {
        quizSessionRepository.nextState()
    }

    suspend fun selectAnswer(choice: Choice) {
        quizSessionRepository.selectAnswer(choice)
    }
}

interface QuizSessionRepository {
    fun observeSession(): Flow<QuizSession?>
    suspend fun getQuizById(quizId: String): QuizSession?
    suspend fun initSession(quiz: Quiz)
    suspend fun nextState()
    suspend fun selectAnswer(choice: Choice)
}

class QuizSessionRepositoryImpl : QuizSessionRepository {
    private val session: MutableStateFlow<QuizSession?> = MutableStateFlow(null)

    override fun observeSession() = session.asStateFlow()

    override suspend fun initSession(quiz: Quiz) {
        val qandas = quiz.qandas.shuffled()
        session.value = QuizSession(
            quiz = quiz.copy(qandas = qandas),
            currentShuffledAnswers = qandas.first().answers.shuffled()
        )
    }

    override suspend fun getQuizById(quizId: String): QuizSession? = session.value

    override suspend fun nextState() {
        val quizSession = session.value
        requireNotNull(quizSession) {
            "Session is not initialized"
        }

        val nextIndex = quizSession.currentIndex +1
        val nextQanda = quizSession.quiz.qandas.getOrNull(nextIndex)
        session.update {
            it?.copy(
                currentIndex = nextIndex,
                currentShuffledAnswers = nextQanda?.answers?.shuffled()
            )
        }
    }

    override suspend fun selectAnswer(choice: Choice) {
        requireNotNull(session.value) {
            "Session is not initialized"
        }

        session.update {
            it?.copy(userAnswers = it.userAnswers + (it.currentIndex to choice))
        }
    }
}