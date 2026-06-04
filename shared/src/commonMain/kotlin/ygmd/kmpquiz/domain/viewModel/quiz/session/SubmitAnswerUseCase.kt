package ygmd.kmpquiz.domain.viewModel.quiz.session

import ygmd.kmpquiz.domain.model.quiz.session.QuizSession
import ygmd.kmpquiz.domain.model.quiz.session.QuizSession.SessionState.Completed
import ygmd.kmpquiz.domain.model.quiz.session.QuizSession.SessionState.InProgress
import ygmd.kmpquiz.domain.model.quiz.session.nextState
import ygmd.kmpquiz.domain.repository.SessionRepository

class SubmitAnswerUseCase(
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(session: QuizSession?, answerId: String) {
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
    suspend operator fun invoke(session: QuizSession?) {
        if(session == null || session.state is Completed) return
        sessionRepository.updateSessionState(session.sessionId.id, session.nextState())
    }
}