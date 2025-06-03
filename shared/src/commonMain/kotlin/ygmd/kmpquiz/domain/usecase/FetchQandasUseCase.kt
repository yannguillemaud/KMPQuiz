package ygmd.kmpquiz.domain.usecase

import ygmd.kmpquiz.domain.pojo.InternalQanda

interface FetchQandasUseCase {
    suspend fun fetch(): Result<List<InternalQanda>>
}