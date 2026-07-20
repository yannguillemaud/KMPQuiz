package ygmd.kmpquiz.presentation.viewModel.quiz.session

import ygmd.kmpquiz.core.domain.qanda.QuestionContent.ImageContent
import ygmd.kmpquiz.core.domain.qanda.QuestionContent.TextContent
import ygmd.kmpquiz.core.domain.session.SessionQuestionReview
import ygmd.kmpquiz.presentation.viewModel.quiz.session.QandaUiState.QuestionUiState.ImageQuestionUiState
import ygmd.kmpquiz.presentation.viewModel.quiz.session.QandaUiState.QuestionUiState.TextQuestionUiState

/**
 * Maps resolved [SessionQuestionReview]s into render-ready `(QandaUiState, UserAnswerState)`
 * pairs. The category name is already resolved inside each review, so this is a pure,
 * non-suspend mapping.
 *
 * Answer order is the qanda's own (unshuffled) order — this is review, not live play, so it
 * must NOT reuse [QuizSessionViewModel.started]'s id-seeded shuffle. Shared by
 * [DetailedSessionViewModel] (session history detail) and [CategoryReviewViewModel]
 * (per-category review), both of which produced this exact mapping inline before.
 */
internal fun List<SessionQuestionReview>.toUserAnswerUiStates(): List<Pair<QandaUiState, UserAnswerState>> =
    map { review ->
        val qanda = review.qanda
        val qandaState = QandaUiState(
            category = review.category.name,
            question = when (qanda.question) {
                is ImageContent -> ImageQuestionUiState(
                    imageUrl = qanda.question.imageUrl,
                    altText = qanda.question.altText,
                )

                is TextContent -> TextQuestionUiState(qanda.question.text)
            },
            answers = qanda.answers.map {
                QandaUiState.AnswerUiState(it.id, it.contextKey)
            },
            correctAnswerId = qanda.answers.correctAnswer.id,
        )
        val answer = UserAnswerState(
            userAnswerContent = qanda.answers.first { it.id == review.userAnswer.answerId.id }
                .let {
                    QandaUiState.AnswerUiState(it.id, it.contextKey)
                },
            correctAnswerContent = qanda.answers.correctAnswer.let {
                QandaUiState.AnswerUiState(it.id, it.contextKey)
            },
        )
        qandaState to answer
    }
