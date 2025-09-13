package ygmd.kmpquiz.domain.usecase.qanda

import co.touchlab.kermit.Logger
import ygmd.kmpquiz.domain.repository.DraftQanda
import ygmd.kmpquiz.domain.repository.QandaRepository

private val logger = Logger.withTag("SaveQandasUseCaseImpl")

class SaveQandasUseCase(
    private val repository: QandaRepository,
) {
    suspend fun saveAll(qandas: List<DraftQanda>): Result<Unit> {
        logger.i { "Attempting to save ${qandas.size} qandas" }
        return repository.saveAll(qandas)
            .fold(
                onSuccess = {
                    logger.i { "Successfully saved: size = ${repository.getAll().size}" }
                    Result.success(Unit)
                },
                onFailure = { error ->
                    logger.e(error) { "Failed to save qandas" }
                    Result.failure(error)
                }
            )
    }

}