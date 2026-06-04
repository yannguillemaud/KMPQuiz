package ygmd.kmpquiz.domain.usecase.quizSession

import co.touchlab.kermit.Logger
import kotlinx.collections.immutable.toPersistentList
import ygmd.kmpquiz.domain.model.quiz.session.QandaId
import ygmd.kmpquiz.domain.model.quiz.session.QuizId
import ygmd.kmpquiz.domain.model.quiz.session.QuizSession
import ygmd.kmpquiz.domain.model.quiz.session.SessionId
import ygmd.kmpquiz.domain.repository.QuizRepository
import ygmd.kmpquiz.domain.repository.SessionRepository
import ygmd.kmpquiz.domain.usecase.qanda.GetQandaUseCase
import java.util.UUID

private val logger = Logger.withTag("QuizSessionUseCase")

class SetUpQuizSessionUseCase(
    private val repository: SessionRepository,
    private val getQandaUseCase: GetQandaUseCase,
    private val quizRepository: QuizRepository,
) {
    suspend fun createSession(quizId: String): Result<SessionId> {
        val quiz = quizRepository.getById(quizId).getOrElse {
            logger.e(it) { "Failed to load quiz" }
            return Result.failure(it)
        }
        val qandasId = getQandaUseCase.getForQuiz(quiz.id).map { QandaId(it.id) }
        val sessionId = UUID.randomUUID().toString()
        val session = QuizSession(
            sessionId = SessionId(sessionId),
            quizId = QuizId(quiz.id),
            questions = qandasId.toPersistentList(),
            state = QuizSession.SessionState.NotStarted,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return repository
            .initSession(session.sessionId.id, session)
            .map { session.sessionId }
    }
}