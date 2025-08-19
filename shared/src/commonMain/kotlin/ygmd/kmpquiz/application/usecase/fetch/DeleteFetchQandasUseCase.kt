package ygmd.kmpquiz.application.usecase.fetch

import co.touchlab.kermit.Logger
import ygmd.kmpquiz.domain.repository.DraftQanda
import ygmd.kmpquiz.domain.repository.FetchRepository

private val logger = Logger.withTag("DeleteFetchQandasUseCase")

class DeleteFetchQandasUseCase(
    private val fetchRepository: FetchRepository
) {
    suspend fun delete(draftQandas: List<DraftQanda>): Result<Unit> {
        logger.i { "Deleting ${draftQandas.size} qandas" }
        return fetchRepository.removeFetched(draftQandas)
    }
}