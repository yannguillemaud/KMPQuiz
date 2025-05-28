package ygmd.kmpquiz.domain.useCase.fetch

import ygmd.kmpquiz.domain.pojo.InternalQanda

interface FetchQandaService {
    suspend fun fetch(): Result<List<InternalQanda>>
}