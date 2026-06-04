package ygmd.kmpquiz.domain.usecase.category

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.model.category.Category
import ygmd.kmpquiz.domain.model.category.CategoryWithCount
import ygmd.kmpquiz.domain.repository.CategoryRepository

class CategoryUseCase(
    private val categoryRepository: CategoryRepository
) {
    fun observeCategories(): Flow<List<CategoryWithCount>> = categoryRepository.observeCategoriesWithCount()
    suspend fun getById(id: String): Category? = categoryRepository.getById(id)
    suspend fun save(name: String): Result<String> = categoryRepository.addCategory(name)
    suspend fun delete(categoryId: String) {
        categoryRepository.removeCategory(categoryId)
    }
}