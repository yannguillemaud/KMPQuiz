package ygmd.kmpquiz.data.repository.session

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ygmd.kmpquiz.database.KMPQuizDatabase
import ygmd.kmpquiz.database.Session_answer
import ygmd.kmpquiz.core.domain.session.AnswerId
import ygmd.kmpquiz.core.domain.session.QandaId
import ygmd.kmpquiz.core.domain.session.QuizId
import ygmd.kmpquiz.core.domain.session.Session
import ygmd.kmpquiz.core.domain.session.SessionId
import ygmd.kmpquiz.core.repository.SessionRepository
import kotlin.collections.map

class PersistentSessionRepository(
    database: KMPQuizDatabase,
    private val dispatcher: CoroutineDispatcher
) : SessionRepository {
    private val queries = database.sessionQueries

    override fun observeSessions(): Flow<List<Session>> {
        val sessionsFlow = queries.getSessions().asFlow().mapToList(dispatcher)
        val answersFlow = queries.getAllAnswers().asFlow().mapToList(dispatcher)
        return combine(sessionsFlow, answersFlow) { sessions, answers ->
            val answersBySessionId: Map<String, List<Session_answer>> =
                answers.groupBy { it.session_id }
            sessions.map { session ->
                Session(
                    sessionId = SessionId(session.id),
                    quizId = QuizId(session.quiz_id),
                    questions = session.question_ids.map { QandaId(it) }.toPersistentList(),
                    answers = answersBySessionId[session.id]?.associate {
                        QandaId(it.question_id) to AnswerId(it.answer_id)
                    }.orEmpty().toPersistentMap(),
                    state = session.state,
                    createdAt = session.created_at,
                    updatedAt = session.updated_at
                )
            }
        }
    }

    override fun observeSession(sessionId: String): Flow<Session?> {
        val sessionFlow = queries.getSessionById(sessionId).asFlow().mapToList(dispatcher)
        val sessionAnswersFlow =
            queries.getAnswersBySessionId(sessionId).asFlow().mapToList(dispatcher)
        return combine(sessionFlow, sessionAnswersFlow) { quizSession, quizSessionAnswers ->
            if (quizSession.isEmpty()) return@combine null
            val session = quizSession.first()
            val answers = quizSessionAnswers.groupBy { it.session_id }[session.id].orEmpty()
            Session(
                sessionId = SessionId(session.id),
                quizId = QuizId(session.quiz_id),
                questions = session.question_ids.map { QandaId(it) }.toPersistentList(),
                answers = answers.associate {
                    QandaId(it.question_id) to AnswerId(it.answer_id)
                }.toPersistentMap(),
                state = session.state,
                createdAt = session.created_at,
                updatedAt = session.updated_at
            )
        }
    }

    override suspend fun initSession(
        sessionId: String,
        session: Session
    ): Result<Unit> = withContext(dispatcher) {
        try {
            queries.createSession(
                id = sessionId,
                quiz_id = session.quizId.id,
                state = session.state,
                question_ids = session.questions.map { it.id },
                created_at = session.createdAt,
                updated_at = session.updatedAt
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateSessionState(
        sessionId: String,
        newState: Session.SessionState
    ): Result<Unit> = withContext(dispatcher) {
        try {
            queries.updateSessionState(
                id = sessionId,
                state = newState,
                updated_at = System.currentTimeMillis()
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveAnswer(
        sessionId: String,
        questionId: String,
        answerId: String
    ): Result<Unit> = withContext(dispatcher) {
        try {
            queries.insertAnswer(
                session_id = sessionId,
                question_id = questionId,
                answer_id = answerId
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteSessions() {
        queries.deleteSessions()
    }
}
