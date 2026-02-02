package ygmd.kmpquiz.domain.usecase.qanda

import co.touchlab.kermit.Logger
import ygmd.kmpquiz.domain.model.draftqanda.DraftQanda
import ygmd.kmpquiz.domain.model.qanda.Qanda
import ygmd.kmpquiz.domain.repository.CategoryRepository
import ygmd.kmpquiz.domain.repository.QandaRepository
import ygmd.kmpquiz.domain.result.SaveMultipleQandasResult
import ygmd.kmpquiz.domain.result.SaveQandaResult
import ygmd.kmpquiz.domain.result.UpdateResult
import java.util.UUID

private val logger = Logger.withTag("SaveQandasUseCaseImpl")

class SaveQandasUseCase(
    private val qandaRepository: QandaRepository,
    private val categoryRepository: CategoryRepository,
) {
    suspend fun save(draft: DraftQanda): SaveQandaResult {
        logger.i { "Attempting to save single $draft" }
        val existingCategory = categoryRepository.getByName(draft.categoryName)
        existingCategory.onFailure {
            categoryRepository.addCategory(draft.categoryName)
                .onFailure { return SaveQandaResult.Error(it) }
        }

        val result = qandaRepository.save(mapToQanda(draft))
        when(result){
            is SaveQandaResult.Success -> logger.i { "Successfully saved $draft" }
            is SaveQandaResult.AlreadyExists -> logger.w { "Failed to save $draft, already exist" }
            is SaveQandaResult.Error -> logger.e(result.error) { "Failed to save $draft" }
        }
        return result
    }

    suspend fun saveAll(draftQandas: List<DraftQanda>): Result<Unit> {
        logger.i { "Attempting to save ${draftQandas.size} qandas" }
        val unknownCategories = draftQandas.map { it.categoryName }
            .distinct()
            .filter {
                categoryRepository.getByName(it).isFailure
            }
        if(unknownCategories.isNotEmpty()){
            unknownCategories.map { category ->
                categoryRepository.addCategory(category)
                    .onFailure {
                        logger.e(it) { "Failed to save category: $category" }
                        return Result.failure(it)
                    }
            }
        }
        val qandas = draftQandas.map { mapToQanda(it) }
        when(val result = qandaRepository.saveAll(qandas)){
            is SaveMultipleQandasResult.AlreadyExist -> {
                logger.w { "Failed to save qandas with ids ${result.existingQandasIds}, already exist" }
                return if(result.existingQandasIds.size == draftQandas.size){
                    Result.failure(Exception("All qandas already exist"))
                } else {
                    Result.success(Unit)
                }
            }
            is SaveMultipleQandasResult.GenericError -> {
                logger.e(result.error) { "Failed to save qandas" }
                return Result.failure(result.error)
            }
            SaveMultipleQandasResult.Success -> {
                logger.i { "Successfully saved qandas" }
                return Result.success(Unit)
            }
        }
    }

    suspend fun update(qanda: Qanda): Result<Unit> {
        logger.i { "Attempting to save $qanda" }
        val result = qandaRepository.update(qanda)
        return when(result){
            is UpdateResult.GenericError -> {
                logger.e(result.error) { "Failed to save $qanda" }
                Result.failure(result.error)
            }
            is UpdateResult.NotFound -> {
                logger.w { "Failed to save $qanda, not found" }
                Result.failure(Exception("Not found"))
            }
            UpdateResult.Success -> {
                logger.i { "Successfully saved $qanda" }
                Result.success(Unit)
            }
        }
    }

    private fun mapToQanda(draftQanda: DraftQanda): Qanda {
        val category = categoryRepository.getByName(draftQanda.categoryName)
            .getOrNull() ?: error("Category not found: ${draftQanda.categoryName}")

        return Qanda(
            id = UUID.randomUUID().toString(),
            question = draftQanda.question,
            answers = draftQanda.answers,
            categoryId = category.id
        )
    }
}