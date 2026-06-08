package ygmd.kmpquiz.domain.usecase.qanda

import ygmd.kmpquiz.domain.model.category.Category
import ygmd.kmpquiz.domain.model.qanda.Qanda
import ygmd.kmpquiz.domain.usecase.category.CategoryUseCase

data class QandaWithCategory(
    val qanda: Qanda,
    val category: Category,
)

class GetQandaWithCategoryUseCase(
    private val getQandaUseCase: GetQandaUseCase,
    private val categoryUseCase: CategoryUseCase,
) {
   suspend operator fun invoke(qandaId: String): QandaWithCategory? {
       val qanda = getQandaUseCase.getById(qandaId) ?: return null
       val category = categoryUseCase.getById(qanda.categoryId) ?: throw Exception("Category not found")
       return QandaWithCategory(qanda, category)
   }
}

