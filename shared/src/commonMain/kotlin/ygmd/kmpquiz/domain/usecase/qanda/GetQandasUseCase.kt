package ygmd.kmpquiz.domain.usecase.qanda

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.repository.QandaRepository

class GetQandaUseCase(
    private val repository: QandaRepository
) {
    fun observeSaved(): Flow<List<Qanda>> = repository.observeAll()
}

