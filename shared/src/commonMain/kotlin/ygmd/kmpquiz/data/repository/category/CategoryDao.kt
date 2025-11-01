package ygmd.kmpquiz.data.repository.category

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.database.CategoryEntity
import ygmd.kmpquiz.database.KMPQuizDatabase
import ygmd.kmpquiz.domain.dao.CategoryDao

class PersistenceCategoryDao(
    database: KMPQuizDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): CategoryDao {
    private val categoryQueries = database.categoryQueries

    override fun observeCategories(): Flow<List<CategoryEntity>> {
        return categoryQueries.selectAllCategories().asFlow().mapToList(dispatcher)
    }

    override fun getAllCategories(): List<CategoryEntity> {
        return categoryQueries.selectAllCategories().executeAsList()
    }

    override fun getByCategoryId(categoryId: String): CategoryEntity? {
        return categoryQueries.getCategoryById(categoryId).executeAsOneOrNull()
    }

    override fun getByCategoryName(categoryName: String): CategoryEntity? {
        return categoryQueries.getCategoryByName(categoryName).executeAsOneOrNull()
    }

    override fun insertCategory(category: CategoryEntity): String {
        categoryQueries.insertCategory(category)
        return category.id
    }

    override fun deleteCategory(categoryId: String) {
        categoryQueries.deleteById(categoryId)
    }
}