package ygmd.kmpquiz.domain.usecase.qanda

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.model.qanda.Qanda
import ygmd.kmpquiz.domain.repository.QandaRepository

class GetQandaUseCase(
    private val qandaRepository: QandaRepository,
) {
    fun observeSaved(): Flow<List<Qanda>> = qandaRepository.observeAll()
    suspend fun getByCategory(category: String): List<Qanda> = qandaRepository.getByCategory(category)
    suspend fun getById(id: String): Qanda? =
        qandaRepository
            .getById(id)
            .getOrNull()
}

