package ygmd.kmpquiz.domain.usecase.qanda

import co.touchlab.kermit.Logger
import ygmd.kmpquiz.domain.model.qanda.Qanda
import ygmd.kmpquiz.domain.repository.QandaRepository
import ygmd.kmpquiz.domain.result.DeleteResult

private val logger = Logger.withTag("DeleteQandasUseCase")

class DeleteQandasUseCase(
    private val repository: QandaRepository,
) {
    suspend fun delete(qanda: Qanda): DeleteResult {
        logger.i { "Deleting qanda with id: ${qanda.id}" }
        return repository.deleteById(qanda.id)
    }

    suspend fun deleteAll(): DeleteResult {
        logger.i { "Deleting all qandas from repository" }
        return repository.deleteAll()
    }

    suspend fun deleteById(id: String): DeleteResult {
        logger.i { "Deleting qanda $id"}
        return repository.deleteById(id)
    }

    suspend fun deleteByCategory(categodyId: String): DeleteResult {
        logger.i { "Deleting qandas with category $categodyId"}
        return repository.deleteByCategory(categodyId)
    }
}