package ygmd.kmpquiz.data.dao

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.database.Category_entity
import ygmd.kmpquiz.database.SelectAllCategoriesWithCount

interface CategoryDao {
    fun observeCategories(): Flow<List<Category_entity>>
    fun observeCategoriesWithCount(): Flow<List<SelectAllCategoriesWithCount>>
    fun getAllCategories(): List<Category_entity>
    fun getByCategoryId(categoryId: String): Category_entity?
    fun getByCategoryName(categoryName: String): Category_entity?
    fun insertCategory(category: Category_entity): String
    fun deleteCategory(categoryId: String)
}