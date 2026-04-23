package ygmd.kmpquiz.domain.usecase.quizSession

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ygmd.kmpquiz.domain.model.qanda.Choice
import ygmd.kmpquiz.domain.model.qanda.Qanda
import ygmd.kmpquiz.domain.model.quiz.Quiz
import ygmd.kmpquiz.domain.model.quiz.QuizConfigDetails
import ygmd.kmpquiz.domain.model.quiz.QuizSession
import ygmd.kmpquiz.domain.repository.QandaRepository
import ygmd.kmpquiz.domain.repository.QuizRepository

private val logger = Logger.withTag("QuizSessionUseCase")

class QuizSessionUseCase(
    private val quizSessionRepository: QuizSessionRepository,
    private val qandaRepository: QandaRepository,
    private val quizRepository: QuizRepository,
) {
    fun observeSession(): Flow<QuizSession?> = quizSessionRepository.observeSession()

    suspend fun initSession(quizId: String): Result<Unit> {
        val quiz = quizRepository.getQuizById(quizId)
            .onFailure {
                logger.e("Failed to get quiz $quizId", it)
                return Result.failure(it)
            }.getOrThrow()
        val qandas = getQandasForQuiz(quiz)
        quizSessionRepository.initSession(quiz, qandas)
        return Result.success(Unit)
    }

    private suspend fun getQandasForQuiz(quiz: Quiz): List<Qanda> {
        return when (quiz.config) {
            is QuizConfigDetails.TotalLimited -> quiz.categories
                .flatMap { qandaRepository.getByCategory(it.id) }
                .shuffled()
                .take(quiz.config.count)
            is QuizConfigDetails.AllQuestions -> quiz.categories
                .flatMap { qandaRepository.getByCategory(it.id) }
            is QuizConfigDetails.ByCategory -> quiz.config.limitByCategory.flatMap { (categoryId, limit) ->
                qandaRepository.getByCategory(categoryId)
                    .shuffled()
                    .take(limit)
            }
        }
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
    suspend fun initSession(quiz: Quiz, qandas: List<Qanda>)
    suspend fun nextState()
    suspend fun selectAnswer(choice: Choice)
}

class QuizSessionRepositoryImpl : QuizSessionRepository {
    private val session: MutableStateFlow<QuizSession?> = MutableStateFlow(null)

    override fun observeSession() = session.asStateFlow()

    override suspend fun initSession(quiz: Quiz, qandas: List<Qanda>) {
        session.value = QuizSession(
            quiz = quiz,
            qandas = qandas,
            currentShuffledAnswers = qandas.first().answers.shuffled(),
        )
    }

    override suspend fun getQuizById(quizId: String): QuizSession? = session.value

    override suspend fun nextState() {
        val quizSession = session.value
        requireNotNull(quizSession) {
            "Session is not initialized"
        }

        val nextIndex = quizSession.currentIndex + 1
        val nextQanda = quizSession.qandas.getOrNull(nextIndex)
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