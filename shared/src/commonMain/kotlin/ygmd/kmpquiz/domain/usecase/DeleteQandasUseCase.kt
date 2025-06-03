package ygmd.kmpquiz.domain.usecase

import arrow.core.Either
import co.touchlab.kermit.Logger
import ygmd.kmpquiz.domain.pojo.InternalQanda
import ygmd.kmpquiz.domain.repository.QandaRepository

interface DeleteQandasUseCase {
    suspend fun delete(qanda: InternalQanda): Result<Unit>
    suspend fun deleteAll(qandas: List<InternalQanda>): Result<Unit>
}

class DeleteQandasUseCaseImpl(
    private val repository: QandaRepository,
    private val logger: Logger
) : DeleteQandasUseCase {
    override suspend fun delete(qanda: InternalQanda): Result<Unit> {
        val id = qanda.id
            ?: throw IllegalArgumentException("Impossible to delete qanda without id: $qanda")
        return try {
            when(val result = repository.deleteById(id)){
                is Either.Left -> {
                    logger.e { "Could not delete qanda $id: ${result.value.message}" }
                    Result.failure(Exception(result.value.message))
                }
                is Either.Right -> {
                    logger.i { "Deleted qanda $id" }
                    Result.success(Unit)
                }
            }
        } catch (e: Exception) {
            logger.e(e) { "Exception while deleting qanda ${qanda.id}" }
            Result.failure(e)
        }
    }

    override suspend fun deleteAll(qandas: List<InternalQanda>): Result<Unit> {
        logger.i { "Deleting all qandas" }
        return try {
            repository.deleteAll()
            logger.i { "Successfully deleted all qandas" }
            Result.success(Unit)
        } catch (e: Exception) {
            logger.e(e) { "Failed to delete all qandas" }
            Result.failure(e)
        }
    }
}