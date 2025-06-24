package ygmd.kmpquiz.application.usecase.qanda

import co.touchlab.kermit.Logger
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.error.DomainError
import ygmd.kmpquiz.domain.repository.QandaRepository

interface SaveQandasUseCase {
    suspend fun save(qanda: Qanda): Result<Unit>
    suspend fun saveAll(qandas: List<Qanda>): Result<Unit>
}

private val logger = Logger.withTag("SaveQandasUseCaseImpl")
class SaveQandasUseCaseImpl(
    private val repository: QandaRepository,
) : SaveQandasUseCase {
    override suspend fun save(qanda: Qanda): Result<Unit> {
        logger.i { "Attempting to save qanda: ${qanda.question.take(50)}..." }

        // Vérification par ID si présent
        qanda.id?.let { id ->
            repository.findById(id).fold(
                onSuccess = {
                    logger.w { "Qanda already exists with id: $id" }
                    return Result.failure(DomainError.QandaError.AlreadyExists)
                },
                onFailure = { /* OK, n'existe pas par ID */ }
            )
        }

        // Vérification par contenu
        return repository.findByContentKey(qanda).fold(
            onSuccess = { existingQanda ->
                logger.w { "Qanda already exists with same content: ${existingQanda.id}" }
                Result.failure(DomainError.QandaError.AlreadyExists)
            },
            onFailure = {
                // N'existe pas, on peut sauvegarder
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


    override suspend fun saveAll(qandas: List<Qanda>): Result<Unit> {
        logger.i { "Attempting to save ${qandas.size} qandas" }

        if (qandas.isEmpty()) {
            return Result.success(Unit)
        }

        val uniqueQandas = qandas.distinctBy { it.contextKey }
        if (uniqueQandas.size != qandas.size) {
            logger.w { "Removed ${qandas.size - uniqueQandas.size} duplicate qandas from input" }
        }

        val qandasToSave = mutableListOf<Qanda>()
        var existingCount = 0

        uniqueQandas.forEach { qanda ->
            val alreadyExists = when {
                // Vérifier par ID si présent
                qanda.id != null -> repository.findById(qanda.id).isSuccess
                // Sinon vérifier par contenu
                else -> repository.findByContentKey(qanda).isSuccess
            }

            if (alreadyExists) {
                existingCount++
                logger.d { "Skipping existing qanda: ${qanda.question.take(30)}..." }
            } else {
                qandasToSave.add(qanda)
            }
        }

        if (existingCount > 0) {
            logger.i { "Skipped $existingCount existing qandas" }
        }

        if (qandasToSave.isEmpty()) {
            logger.i { "No new qandas to save" }
            return Result.success(Unit)
        }

        return repository.saveAll(qandasToSave).fold(
            onSuccess = {
                logger.i { "Successfully saved ${qandasToSave.size} qandas" }
                Result.success(Unit)
            },
            onFailure = { error ->
                logger.e { "Failed to save qandas: ${error.message}" }
                Result.failure(error)
            }
        )
    }
}