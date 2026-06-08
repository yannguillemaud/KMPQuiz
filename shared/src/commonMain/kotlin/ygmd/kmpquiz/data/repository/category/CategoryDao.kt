package ygmd.kmpquiz.data.repository.category

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.database.Category_entity
import ygmd.kmpquiz.database.KMPQuizDatabase
import ygmd.kmpquiz.database.SelectAllCategoriesWithCount
import ygmd.kmpquiz.domain.dao.CategoryDao

class PersistenceCategoryDao(
    database: KMPQuizDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): CategoryDao {
    private val categoryQueries = database.categoryQueries

    override fun observeCategories(): Flow<List<Category_entity>> {
        return categoryQueries.selectAllCategories().asFlow().mapToList(dispatcher)
    }

    override fun observeCategoriesWithCount(): Flow<List<SelectAllCategoriesWithCount>> {
        return categoryQueries.selectAllCategoriesWithCount().asFlow().mapToList(dispatcher)
    }

    override fun getAllCategories(): List<Category_entity> {
        return categoryQueries.selectAllCategories().executeAsList()
    }

    override fun getByCategoryId(categoryId: String): Category_entity? {
        return categoryQueries.getCategoryById(categoryId).executeAsOneOrNull()
    }

    override fun getByCategoryName(categoryName: String): Category_entity? {
        return categoryQueries.getCategoryByName(categoryName).executeAsOneOrNull()
    }

    override fun insertCategory(category: Category_entity): String {
        categoryQueries.insertCategory(category)
        return category.id
    }

    override fun deleteCategory(categoryId: String) {
        categoryQueries.deleteById(categoryId)
    }
}