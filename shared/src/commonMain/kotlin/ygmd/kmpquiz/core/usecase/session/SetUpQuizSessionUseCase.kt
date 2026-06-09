package ygmd.kmpquiz.core.usecase.session

import co.touchlab.kermit.Logger
import kotlinx.collections.immutable.toPersistentList
import ygmd.kmpquiz.core.domain.session.QandaId
import ygmd.kmpquiz.core.domain.session.QuizId
import ygmd.kmpquiz.core.domain.session.Session
import ygmd.kmpquiz.core.domain.session.SessionId
import ygmd.kmpquiz.core.repository.QuizRepository
import ygmd.kmpquiz.core.repository.SessionRepository
import ygmd.kmpquiz.core.usecase.qanda.GetQandaUseCase
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
        val session = Session(
            sessionId = SessionId(sessionId),
            quizId = QuizId(quiz.id),
            questions = qandasId.toPersistentList(),
            state = Session.SessionState.InProgress(index = 0),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return repository
            .initSession(session.sessionId.id, session)
            .map { session.sessionId }
    }
}