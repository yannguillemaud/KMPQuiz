package ygmd.kmpquiz.presentation.viewModel.quiz.session

import ygmd.kmpquiz.core.domain.session.CategoryStat
import ygmd.kmpquiz.core.usecase.category.CategoryUseCase

/**
 * Maps domain [CategoryStat]s into render-ready [CategoryStatUiState]s, resolving each
 * category id to its display name via [categoryUseCase] (falling back to "Unknown" when
 * the category can no longer be found).
 *
 * Shared by [QuizSessionViewModel] (live finish screen) and [DetailedSessionViewModel]
 * (session history detail), both of which produced this exact mapping inline before.
 */
internal suspend fun List<CategoryStat>.toCategoryStatUiStates(
    categoryUseCase: CategoryUseCase,
): List<CategoryStatUiState> = map { stat ->
    CategoryStatUiState(
        categoryId = stat.categoryId,
        categoryName = categoryUseCase.getById(stat.categoryId)?.name ?: "Unknown",
        totalQuestions = stat.totalQuestions,
        correctAnswers = stat.correctAnswers,
        successRate = stat.successRate,
    )
}
