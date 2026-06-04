package ygmd.kmpquiz.domain.usecase.quizSession

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ygmd.kmpquiz.domain.model.qanda.AnswerContent
import ygmd.kmpquiz.domain.model.qanda.QuestionContent
import ygmd.kmpquiz.domain.model.quiz.session.QandaId
import ygmd.kmpquiz.domain.model.quiz.session.QuizSession
import ygmd.kmpquiz.domain.model.quiz.session.SessionResults
import ygmd.kmpquiz.domain.repository.SessionRepository
import java.time.LocalDateTime
import java.time.ZoneId

data class SummarizedSession(
    val sessionId: String,
    val quizId: String,
    val state: QuizSession.SessionState,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class DetailedSession(
    val sessionId: String,
    val quizId: String,
    val state: QuizSession.SessionState,
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

    fun observeSession(sessionId: String): Flow<QuizSession> {
        val result = sessionRepository.observeSession(sessionId)
        return result.map {
            it ?: throw IllegalStateException("Session $sessionId not found")
        }
    }

    suspend fun deleteSessions() {
        sessionRepository.deleteSessions()
    }
}

private fun Long.localDateTimeOf() = LocalDateTime.ofInstant(
    java.time.Instant.ofEpochMilli(this), ZoneId.systemDefault()
)