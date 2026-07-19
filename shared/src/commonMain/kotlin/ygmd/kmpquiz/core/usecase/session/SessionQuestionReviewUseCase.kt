package ygmd.kmpquiz.core.usecase.session

import ygmd.kmpquiz.core.domain.category.Category
import ygmd.kmpquiz.core.domain.session.SessionQuestionReview
import ygmd.kmpquiz.core.domain.session.SessionResults
import ygmd.kmpquiz.core.usecase.category.CategoryUseCase
import ygmd.kmpquiz.core.usecase.qanda.GetQandaUseCase

/**
 * Resolves the per-question review of an already-computed [SessionResults]: for every
 * answered question it joins the [ygmd.kmpquiz.core.domain.qanda.Qanda] and its
 * [ygmd.kmpquiz.core.domain.category.Category] with the user's answer.
 *
 * An optional [categoryId] filters the review to a single category (used by the category
 * review screen). Questions whose qanda can no longer be resolved (e.g. deleted meanwhile)
 * are silently skipped rather than failing the whole review. A qanda whose category was
 * deleted (categories don't cascade-delete their qandas) is kept with a synthesized
 * "Unknown" category, mirroring [ygmd.kmpquiz.presentation.viewModel.quiz.session] stats
 * mapping so the review list stays consistent with the per-category stats counts.
 */
class SessionQuestionReviewUseCase(
    private val getQandaUseCase: GetQandaUseCase,
    private val categoryUseCase: CategoryUseCase,
) {
    suspend operator fun invoke(
        results: SessionResults,
        categoryId: String? = null,
    ): List<SessionQuestionReview> = results.userResults.mapNotNull { (qandaId, userAnswer) ->
        val qanda = getQandaUseCase.getById(qandaId.id) ?: return@mapNotNull null
        if (categoryId != null && qanda.categoryId != categoryId) return@mapNotNull null
        val category = categoryUseCase.getById(qanda.categoryId)
            ?: Category(id = qanda.categoryId, name = "Unknown")
        SessionQuestionReview(qanda, category, userAnswer)
    }
}
