package ygmd.kmpquiz.core.usecase.category

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.core.domain.category.Category
import ygmd.kmpquiz.core.domain.category.CategoryWithCount
import ygmd.kmpquiz.core.repository.CategoryRepository
import ygmd.kmpquiz.core.repository.QuizRepository

class CategoryUseCase(
    private val quizRepository: QuizRepository,
    private val categoryRepository: CategoryRepository
) {
    fun observeCategories(): Flow<List<CategoryWithCount>> =
        categoryRepository.observeCategoriesWithCount()

    suspend fun getById(id: String): Category? = categoryRepository.getById(id)

    /**
     * Returns the number of quizzes that use the given category.
     */
    suspend fun countCategoryInUse(categoryId: String): Int {
        return quizRepository.countCategoryInUse(categoryId)
    }

    /**
     * Returns the number of DISTINCT quizzes that use any of the given categories.
     * Distinct so a quiz shared across several selected categories is counted once —
     * used to warn before a bulk cascade delete.
     */
    suspend fun countCategoriesInUse(categoryIds: Set<String>): Int {
        return quizRepository.countCategoriesInUse(categoryIds)
    }

    suspend fun delete(categoryId: String): Result<Unit> {
        return quizRepository.removeCategory(categoryId)
            .onSuccess { categoryRepository.removeCategory(categoryId) }
    }
}