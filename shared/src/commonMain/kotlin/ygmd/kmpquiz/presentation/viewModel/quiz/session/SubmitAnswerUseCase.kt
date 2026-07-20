package ygmd.kmpquiz.presentation.viewModel.quiz.session

import co.touchlab.kermit.Logger
import ygmd.kmpquiz.core.domain.session.Session
import ygmd.kmpquiz.core.domain.session.Session.SessionState.Completed
import ygmd.kmpquiz.core.domain.session.Session.SessionState.InProgress
import ygmd.kmpquiz.core.domain.session.nextState
import ygmd.kmpquiz.core.repository.SessionRepository

class SubmitAnswerUseCase(
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(session: Session?, answerId: String) {
        if (session == null || session.state !is InProgress) return
        sessionRepository.saveAnswer(
            sessionId = session.sessionId.id,
            questionId = session.questions[session.state.index].id,
            answerId = answerId
        )
    }
}

class NextStateSessionUseCase(
    private val sessionRepository: SessionRepository,
    private val logger: Logger = Logger.withTag("NextStateSessionUseCase"),
) {
    suspend operator fun invoke(session: Session?) {
        if(session == null) {
            logger.w { "Cannot invoke NextStateSessionUseCase with null session" }
            return
        }
        sessionRepository.updateSessionState(session.sessionId.id, session.nextState())
    }
}