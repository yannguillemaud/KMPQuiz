package ygmd.kmpquiz.domain.usecase.qanda

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.model.qanda.Qanda
import ygmd.kmpquiz.domain.repository.QandaRepository

class GetQandaUseCase(
    private val repository: QandaRepository
) {
    fun observeSaved(): Flow<List<Qanda>> = repository.observeAll()
    suspend fun getByCategory(category: String): List<Qanda> = repository.getByCategory(category)
    suspend fun getById(id: String): Qanda? =
        repository
            .getById(id)
            .getOrNull()
}

