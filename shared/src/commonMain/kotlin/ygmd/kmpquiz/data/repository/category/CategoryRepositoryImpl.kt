package ygmd.kmpquiz.data.repository.category

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ygmd.kmpquiz.database.Category_entity
import ygmd.kmpquiz.domain.dao.CategoryDao
import ygmd.kmpquiz.domain.model.category.Category
import ygmd.kmpquiz.domain.model.category.CategoryWithCount
import ygmd.kmpquiz.domain.repository.CategoryRepository
import java.util.UUID

private val logger = Logger.withTag("${CategoryRepositoryImpl::class.simpleName}")

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
) : CategoryRepository {
    override fun observeCategories(): Flow<List<Category>> {
        return categoryDao.observeCategories()
            .map { it.map { category -> Category(category.id, category.name) } }
    }

    override fun observeCategoriesWithCount(): Flow<List<CategoryWithCount>> {
        return categoryDao.observeCategoriesWithCount().map {
            it.map { categoryWithCount ->
                CategoryWithCount(
                    categoryWithCount.id,
                    categoryWithCount.name,
                    categoryWithCount.questionCount.toInt()
                )
            }
        }
    }

    override suspend fun addCategory(name: String): Result<String> {
        val existingCategory = categoryDao.getByCategoryName(name)
        if (existingCategory != null) {
            logger.w { "Category $name already exists" }
            return Result.success(existingCategory.id)
        }
        val id = UUID.randomUUID().toString()
        categoryDao.insertCategory(Category_entity(id, name))
        return Result.success(id)
    }

    override suspend fun removeCategory(id: String): Result<Unit> {
        return try {
            categoryDao.deleteCategory(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getById(id: String): Category? {
        return categoryDao.getByCategoryId(id)?.let {
            Category(it.id, it.name)
        }
    }

    override suspend fun getByName(name: String): Category? {
        return categoryDao.getByCategoryName(name)
            ?.let { Category(it.id, it.name) }
    }

    override suspend fun getAllCategories(): List<Category> {
        return categoryDao.getAllCategories().map {
            Category(it.id, it.name)
        }
    }
}