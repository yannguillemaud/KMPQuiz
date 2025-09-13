package ygmd.kmpquiz.domain.usecase.fetch

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.repository.DraftQanda
import ygmd.kmpquiz.domain.repository.FetchRepository

class GetFetchQandasUseCase(
    private val repository: FetchRepository,
) {
    fun observeFetched(): Flow<List<DraftQanda>> = repository.observeFetched()
}