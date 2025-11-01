package ygmd.kmpquiz.domain.repository

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.model.category.Category

interface CategoryRepository {
    fun observeCategories(): Flow<List<Category>>
    fun addCategory(name: String): Result<Unit>
    fun removeCategory(id: String): Result<Unit>
    fun getById(id: String): Result<Category>
    fun getByName(name: String): Result<Category>
    fun getAllCategories(): List<Category>
}