package ygmd.kmpquiz.core.domain.session

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable
import ygmd.kmpquiz.core.domain.session.Session.SessionState.Completed
import ygmd.kmpquiz.core.domain.session.Session.SessionState.InProgress

@Serializable
data class SessionId(val id: String)

@Serializable
data class QuizId(val id: String)

@Serializable
data class QandaId(val id: String)

@Serializable
data class AnswerId(val id: String)

@Serializable
data class Session(
    val sessionId: SessionId,
    val quizId: QuizId,
    val questions: PersistentList<QandaId> = persistentListOf(),
    val answers: PersistentMap<QandaId, AnswerId> = persistentMapOf(),
    val state: SessionState,
    val createdAt: Long,
    val updatedAt: Long
){
    @Serializable
    sealed interface SessionState {
        @Serializable
        data class InProgress(val index: Int) : SessionState
        @Serializable
        object Completed : SessionState
    }
}

fun Session.nextState(): Session.SessionState = when (state) {
    is Completed -> Completed
    is InProgress -> {
        if (state.index == questions.size - 1) Completed
        else InProgress(state.index + 1)
    }
}