package ygmd.kmpquiz.domain.viewModel.displayable

import ygmd.kmpquiz.domain.model.qanda.Answers
import ygmd.kmpquiz.domain.model.qanda.Choice
import ygmd.kmpquiz.domain.model.quiz.QuizResult
import ygmd.kmpquiz.domain.model.quiz.QuizSession

sealed interface DisplayableQuizSession {
    val session: QuizSession
    val title: String
    val index: Int
    val size: Int

    data class InProgress(
        override val session: QuizSession,
        val selectedAnswer: Choice?,
        val currentQanda: DisplayableQanda,
        val shuffledAnswers: Answers,
    ) : DisplayableQuizSession {
        val hasAnswered: Boolean
            get() = selectedAnswer != null

        override val title: String
            get() = session.quiz.title

        override val index: Int
            get() = session.currentIndex

        override val size: Int
            get() = session.quiz.qandas.size
    }

    data class Completed(
        override val session: QuizSession,
        val results: QuizResult,
    ) : DisplayableQuizSession {
        override val title: String
            get() = session.quiz.title

        override val index: Int
            get() = session.currentIndex +1

        override val size: Int
            get() = session.quiz.qandas.size
    }
}