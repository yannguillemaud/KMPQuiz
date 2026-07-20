package ygmd.kmpquiz.core.usecase.session

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ygmd.kmpquiz.core.domain.qanda.AnswerContent
import ygmd.kmpquiz.core.domain.qanda.QuestionContent
import ygmd.kmpquiz.core.domain.session.QandaId
import ygmd.kmpquiz.core.domain.session.Session
import ygmd.kmpquiz.core.domain.session.SessionResults
import ygmd.kmpquiz.core.repository.SessionRepository
import java.time.LocalDateTime
import java.time.ZoneId

data class SummarizedSession(
    val sessionId: String,
    val quizId: String,
    val state: Session.SessionState,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class DetailedSession(
    val sessionId: String,
    val quizId: String,
    val state: Session.SessionState,
    val quizTitle: String,
    val questions: List<SessionQanda>,
    val answers: List<SessionResults.UserAnswer>,
)

data class SessionQanda(
    val id: QandaId,
    val question: QuestionContent,
    val answers: List<AnswerContent>,
)

class ObserveSessionUseCase(
    private val sessionRepository: SessionRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeSummarizedSessions(): Flow<List<SummarizedSession>> =
        sessionRepository.observeSessions()
            .map {
                it.map { session ->
                    SummarizedSession(
                        sessionId = session.sessionId.id,
                        quizId = session.quizId.id,
                        state = session.state,
                        createdAt = session.createdAt.localDateTimeOf(),
                        updatedAt = session.updatedAt.localDateTimeOf()
                    )
                }
            }

    fun observeSession(sessionId: String): Flow<Session?> {
        return sessionRepository.observeSession(sessionId)
    }

    suspend fun deleteSessions() {
        sessionRepository.deleteSessions()
    }
}

private fun Long.localDateTimeOf() = LocalDateTime.ofInstant(
    java.time.Instant.ofEpochMilli(this), ZoneId.systemDefault()
)