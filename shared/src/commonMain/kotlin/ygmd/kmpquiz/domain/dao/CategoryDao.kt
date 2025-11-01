package ygmd.kmpquiz.domain.dao

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.database.CategoryEntity

interface CategoryDao {
    fun observeCategories(): Flow<List<CategoryEntity>>
    fun getAllCategories(): List<CategoryEntity>
    fun getByCategoryId(categoryId: String): CategoryEntity?
    fun getByCategoryName(categoryName: String): CategoryEntity?
    fun insertCategory(category: CategoryEntity): String
    fun deleteCategory(categoryId: String)
}