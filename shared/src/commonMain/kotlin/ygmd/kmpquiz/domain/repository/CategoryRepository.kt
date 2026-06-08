package ygmd.kmpquiz.domain.repository

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.model.category.Category
import ygmd.kmpquiz.domain.model.category.CategoryWithCount

interface CategoryRepository {
    fun observeCategories(): Flow<List<Category>>
    fun observeCategoriesWithCount(): Flow<List<CategoryWithCount>>
    suspend fun addCategory(name: String): Result<String>
    suspend fun removeCategory(id: String): Result<Unit>
    suspend fun getById(id: String): Category?
    suspend fun getByName(name: String): Category?
    suspend fun getAllCategories(): List<Category>
}