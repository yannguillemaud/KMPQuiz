package ygmd.kmpquiz.application.usecase.qanda

import co.touchlab.kermit.Logger
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.error.DomainError.PersistenceError.DatabaseError
import ygmd.kmpquiz.domain.repository.QandaRepository
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

private val logger = Logger.withTag("DeleteQandasUseCase")

class DeleteQandasUseCase(
    private val repository: QandaRepository,
) {
    suspend fun delete(qanda: Qanda): Result<Unit> {
        logger.i { "Deleting qanda with id: ${qanda.id}" }
        val id = qanda.id

        return repository.deleteById(id).fold(
            onSuccess = {
                logger.i { "Successfully deleted qanda ${qanda.id}" }
                success(Unit)
            },
            onFailure = {
                logger.e { "Could not delete qanda ${qanda.id}" }
                val errorMessage = it.message ?: "Unknown error"
                failure(DatabaseError(errorMessage))
            }
        )
    }

    suspend fun deleteAll() {
        logger.i { "Deleting all qandas from repository" }
        repository.deleteAll()
    }

    suspend fun deleteById(id: String): Result<Unit> {
        logger.i { "Deleting qanda $id"}
        return repository.deleteById(id).fold(
            onSuccess = {
                logger.i { "Successfully deleted"}
                success(Unit)
            },
            onFailure = {
                logger.e(it){ "Failed: ${it.message}"}
                failure(it)
            }
        )
    }

    suspend fun deleteAllByCategory(category: String){
        repository.getAll().filter { it.metadata.category == category }
            .forEach { delete(it) }
    }
}