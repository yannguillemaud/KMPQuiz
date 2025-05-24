package ygmd.kmpquiz.domain.useCase.fetch

import ygmd.kmpquiz.domain.pojo.InternalQanda

interface FetchQandas {
    suspend fun fetch(): Result<List<InternalQanda>>
}