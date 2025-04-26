package ygmd.kmpquiz.data.repository.category

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ygmd.kmpquiz.database.CategoryEntity
import ygmd.kmpquiz.domain.dao.CategoryDao
import ygmd.kmpquiz.domain.model.category.Category
import ygmd.kmpquiz.domain.repository.CategoryRepository
import java.util.UUID

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
): CategoryRepository {
    override fun observeCategories(): Flow<List<Category>> {
        return categoryDao.observeCategories()
            .map { it.map { entity -> Category(entity.id, entity.name) } }
    }

    override fun addCategory(name: String): Result<Unit> {
        if(categoryDao.getByCategoryName(name) != null){
            return Result.failure(Exception("Category already exists"))
        }
        categoryDao.insertCategory(CategoryEntity(UUID.randomUUID().toString(), name))
        return Result.success(Unit)
    }

    override fun removeCategory(id: String): Result<Unit> {
        return try {
            categoryDao.deleteCategory(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getById(id: String): Result<Category> {
        return categoryDao.getByCategoryId(id)
            ?.let { Result.success(Category(it.id, it.name)) }
            ?: Result.failure(Exception("Category not found"))
    }

    override fun getByName(name: String): Result<Category> {
        return categoryDao.getByCategoryName(name)
            ?.let { Result.success(Category(it.id, it.name)) }
            ?: Result.failure(Exception("Category not found"))
    }

    override fun getAllCategories(): List<Category> {
        return categoryDao.getAllCategories().map {
            Category(it.id, it.name)
        }
    }
}