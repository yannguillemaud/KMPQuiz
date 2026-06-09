package ygmd.kmpquiz.core.repository

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.core.domain.category.Category
import ygmd.kmpquiz.core.domain.category.CategoryWithCount

interface CategoryRepository {
    fun observeCategories(): Flow<List<Category>>
    fun observeCategoriesWithCount(): Flow<List<CategoryWithCount>>
    suspend fun saveCategory(id: String, name: String): Result<Unit>
    suspend fun removeCategory(id: String): Result<Unit>
    suspend fun getById(id: String): Category?
    suspend fun getByName(name: String): Category?
    suspend fun getAllCategories(): List<Category>
}