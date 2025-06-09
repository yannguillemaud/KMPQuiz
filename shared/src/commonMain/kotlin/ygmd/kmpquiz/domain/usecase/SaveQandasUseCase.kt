package ygmd.kmpquiz.domain.usecase

import co.touchlab.kermit.Logger
import ygmd.kmpquiz.domain.error.DomainError
import ygmd.kmpquiz.domain.pojo.InternalQanda
import ygmd.kmpquiz.domain.repository.qanda.QandaRepository

interface SaveQandasUseCase {
    suspend fun save(qanda: InternalQanda): Result<Unit>
    suspend fun saveAll(qandas: List<InternalQanda>): Result<Unit>
}

class SaveQandasUseCaseImpl(
    private val repository: QandaRepository,
    private val logger: Logger
) : SaveQandasUseCase {
    override suspend fun save(qanda: InternalQanda): Result<Unit> {
        logger.i { "Attempting to save qanda: ${qanda.question.take(50)}..." }
        val existing = repository.existsByContentKey(qanda)
        return existing.fold(
            onSuccess = {
                logger.w { "Qanda already exists with same content" }
                Result.failure(DomainError.QandaError.AlreadyExists)
            },
            onFailure = {
                repository.save(qanda).fold(
                    onSuccess = { id ->
                        logger.i { "Successfully saved qanda with id: $id" }
                        Result.success(Unit)
                    },
                    onFailure = { error ->
                        logger.e { "Failed to save qanda: ${error.message}" }
                        Result.failure(error)
                    }
                )
            }
        )
    }

    override suspend fun saveAll(qandas: List<InternalQanda>): Result<Unit> {
        logger.i { "Attempting to save ${qandas.size} qandas" }

        if (qandas.isEmpty()) {
            return Result.success(Unit)
        }

        val uniqueQandas = qandas.distinctBy { it.contentKey }

        if (uniqueQandas.size != qandas.size) {
            logger.w { "Removed ${qandas.size - uniqueQandas.size} duplicate qandas" }
        }

        return repository.saveAll(uniqueQandas).fold(
            onSuccess = {
                logger.i { "Successfully saved ${uniqueQandas.size} qandas" }
                Result.success(Unit)
            },
            onFailure = { error ->
                logger.e { "Failed to save qandas: ${error.message}" }
                Result.failure(error)
            }
        )
    }
}