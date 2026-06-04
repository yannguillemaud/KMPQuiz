package ygmd.kmpquiz.domain.repository

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.model.quiz.session.QuizSession


interface SessionRepository {
    fun observeSessions(): Flow<List<QuizSession>>

    fun observeSession(sessionId: String): Flow<QuizSession?>

    suspend fun initSession(
        sessionId: String,
        session: QuizSession
    ): Result<Unit>

    suspend fun updateSessionState(
        sessionId: String,
        newState: QuizSession.SessionState
    ): Result<Unit>

    suspend fun saveAnswer(
        sessionId: String,
        questionId: String,
        answerId: String
    ): Result<Unit>

    suspend fun deleteSessions()
}