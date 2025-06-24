package ygmd.kmpquiz.application.usecase.qanda

import co.touchlab.kermit.Logger
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.error.DomainError.PersistenceError.DatabaseError
import ygmd.kmpquiz.domain.repository.QandaRepository
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

interface DeleteQandasUseCase {
    suspend fun delete(qanda: Qanda): Result<Unit>
    suspend fun deleteAll(qandas: List<Qanda>): Result<Unit>
    suspend fun deleteAll(): Result<Unit>
}

private val logger = Logger.withTag("DeleteQandasUseCase")

class DeleteQandasUseCaseImpl(
    private val repository: QandaRepository,
) : DeleteQandasUseCase {
    override suspend fun delete(qanda: Qanda): Result<Unit> {
        logger.i { "Deleting qanda with id: ${qanda.id}" }
        val id = qanda.id
            ?: return failure(IllegalArgumentException("Impossible to delete qanda without id: $qanda"))

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

    override suspend fun deleteAll(qandas: List<Qanda>): Result<Unit> {
        logger.i { "Deleting ${qandas.size} qandas" }

        if (qandas.isEmpty()) {
            return success(Unit)
        }

        val failures = mutableListOf<Throwable>()
        var successCount = 0

        for (qanda in qandas) {
            delete(qanda).fold(
                onSuccess = { successCount++ },
                onFailure = { failures.add(it) }
            )
        }

        return if (failures.isEmpty()) {
            logger.i { "Successfully deleted all $successCount qandas" }
            success(Unit)
        } else {
            val message = "Failed to delete ${failures.size}/${qandas.size} qandas"
            logger.e { message }
            failure(DatabaseError(message))
        }
    }

    override suspend fun deleteAll(): Result<Unit> {
        logger.i { "Deleting all qandas from repository" }
        return repository.deleteAll().fold(
            onSuccess = {
                logger.i { "Successfully deleted all qandas" }
                success(Unit)
            },
            onFailure = { error ->
                logger.e { "Failed to delete all qandas: ${error.message}" }
                failure(error)
            }
        )
    }
}