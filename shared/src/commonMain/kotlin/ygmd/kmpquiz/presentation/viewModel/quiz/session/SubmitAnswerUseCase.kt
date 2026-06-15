package ygmd.kmpquiz.presentation.viewModel.quiz.session

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
) {
    suspend operator fun invoke(session: Session?) {
        if(session == null || session.state is Completed) return
        sessionRepository.updateSessionState(session.sessionId.id, session.nextState())
    }
}