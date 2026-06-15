package ygmd.kmpquiz.core.repository

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.core.domain.session.Session


interface SessionRepository {
    fun observeSessions(): Flow<List<Session>>

    fun observeSession(sessionId: String): Flow<Session?>

    suspend fun initSession(
        sessionId: String,
        session: Session
    ): Result<Unit>

    suspend fun updateSessionState(
        sessionId: String,
        newState: Session.SessionState
    ): Result<Unit>

    suspend fun saveAnswer(
        sessionId: String,
        questionId: String,
        answerId: String
    ): Result<Unit>

    suspend fun deleteSessions()
}