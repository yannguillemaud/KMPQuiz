package ygmd.kmpquiz.domain.usecase.category

import co.touchlab.kermit.Logger
import ygmd.kmpquiz.domain.repository.CategoryRepository

private val logger = Logger.withTag("CategoryUseCase")

class CategoryUseCase(
    private val categoryRepository: CategoryRepository
){
    fun observeCategories() = categoryRepository.observeCategories()
    fun getById(id: String) = categoryRepository.getById(id).getOrThrow()
    fun save(name: String) = categoryRepository.addCategory(name)
    fun delete(categoryId: String) = categoryRepository.removeCategory(categoryId)
}