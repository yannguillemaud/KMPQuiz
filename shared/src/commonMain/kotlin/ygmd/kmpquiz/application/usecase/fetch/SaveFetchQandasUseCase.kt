package ygmd.kmpquiz.application.usecase.fetch

import ygmd.kmpquiz.domain.repository.DraftQanda
import ygmd.kmpquiz.domain.repository.FetchRepository

class SaveFetchQandasUseCase(
    private val fetchRepository: FetchRepository,
) {
    suspend fun saveFetched(qandas: List<DraftQanda>) {
        fetchRepository.saveDrafted(qandas)
    }

    suspend fun removeFetched(qandas: List<DraftQanda>){
        fetchRepository.removeFetched(qandas)
    }
}