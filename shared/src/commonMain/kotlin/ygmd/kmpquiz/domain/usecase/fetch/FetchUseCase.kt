package ygmd.kmpquiz.domain.usecase.fetch

import co.touchlab.kermit.Logger
import ygmd.kmpquiz.data.repository.fetch.FetchRepository
import ygmd.kmpquiz.domain.model.draftqanda.DraftQanda
import ygmd.kmpquiz.domain.result.FetchResult

private val logger = Logger.withTag("FetchUseCase")

class FetchUseCase(private val fetchRepository: FetchRepository) {
    suspend operator fun invoke(): FetchResult<List<DraftQanda>> {
        val fetchers = fetchRepository.getAll().map { it.fetcher }
        if(fetchers.size > 1) {
            logger.w { "Multiple fetching is not yet handled. Only one fetch will perform" }
        }
        return fetchers.first().fetch()
    }
}