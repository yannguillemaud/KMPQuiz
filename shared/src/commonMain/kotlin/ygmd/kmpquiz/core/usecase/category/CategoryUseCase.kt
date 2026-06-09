package ygmd.kmpquiz.core.usecase.category

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.core.domain.category.Category
import ygmd.kmpquiz.core.domain.category.CategoryWithCount
import ygmd.kmpquiz.core.repository.CategoryRepository

class CategoryUseCase(
    private val categoryRepository: CategoryRepository
) {
    fun observeCategories(): Flow<List<CategoryWithCount>> = categoryRepository.observeCategoriesWithCount()
    suspend fun getById(id: String): Category? = categoryRepository.getById(id)
    suspend fun delete(categoryId: String) {
        categoryRepository.removeCategory(categoryId)
    }
}