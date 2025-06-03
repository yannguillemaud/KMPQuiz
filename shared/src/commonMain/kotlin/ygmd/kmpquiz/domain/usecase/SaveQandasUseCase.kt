package ygmd.kmpquiz.domain.usecase

import arrow.core.Either
import co.touchlab.kermit.Logger
import ygmd.kmpquiz.domain.pojo.InternalQanda
import ygmd.kmpquiz.domain.repository.QandaRepository

interface SaveQandasUseCase {
    suspend fun save(qanda: InternalQanda): Result<Unit>
    suspend fun saveAll(qandas: List<InternalQanda>): Result<Unit>
}

class SaveQandasUseCaseImpl(
    private val repository: QandaRepository,
    private val logger: Logger
) : SaveQandasUseCase {
    override suspend fun save(qanda: InternalQanda): Result<Unit> {
        logger.i { "Trying to save qanda: ${qanda.id}" }
        return try {
            when(val result = repository.save(qanda)) {
                is Either.Left -> {
                    logger.e { "Failed to save qanda ${qanda.id}: ${result.value}" }
                    Result.failure(Exception("Failed to save qanda: ${result.value}"))
                }
                is Either.Right -> {
                    logger.i { "Successfully saved qanda: ${qanda.id}" }
                    Result.success(Unit)
                }
            }
        } catch (e: Exception) {
            logger.e(e) { "Exception while saving qanda ${qanda.id}" }
            Result.failure(e)
        }
    }

    override suspend fun saveAll(qandas: List<InternalQanda>): Result<Unit> {
        logger.i { "Trying to save ${qandas.size} qandas" }
        return try {
            val failures = mutableListOf<String>()

            qandas.forEach { qanda ->
                when (val result = repository.save(qanda)) {
                    is Either.Right -> { /* Continue */ }
                    is Either.Left -> {
                        failures.add("Failed to save qanda ${qanda.id}: ${result.value.message}")
                    }
                }
            }

            if (failures.isEmpty()) {
                logger.i { "Successfully saved all ${qandas.size} qandas" }
                Result.success(Unit)
            } else {
                logger.e { "Failed to save some qandas: ${failures.joinToString(", ")}" }
                Result.failure(Exception("Failed to save ${failures.size} out of ${qandas.size} qandas"))
            }
        } catch (e: Exception) {
            logger.e(e) { "Exception while saving qandas batch" }
            Result.failure(e)
        }
    }
}