package ygmd.kmpquiz.core.domain.session

import ygmd.kmpquiz.core.domain.category.Category
import ygmd.kmpquiz.core.domain.qanda.Qanda

/**
 * A single answered question, resolved for review after a completed session: the full
 * [Qanda], the [Category] it belongs to, and the [SessionResults.UserAnswer] the player gave.
 *
 * Produced by [ygmd.kmpquiz.core.usecase.session.SessionQuestionReviewUseCase] and mapped
 * into render-ready UI state in the presentation layer.
 */
data class SessionQuestionReview(
    val qanda: Qanda,
    val category: Category,
    val userAnswer: SessionResults.UserAnswer,
)
