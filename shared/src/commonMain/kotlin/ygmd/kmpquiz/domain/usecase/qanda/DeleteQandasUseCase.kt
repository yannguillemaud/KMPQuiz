package ygmd.kmpquiz.domain.usecase.qanda

import ygmd.kmpquiz.domain.model.qanda.Qanda
import ygmd.kmpquiz.domain.repository.QandaRepository

class DeleteQandasUseCase(private val repository: QandaRepository) {
    suspend fun delete(qanda: Qanda): Result<Unit> = repository.deleteById(qanda.id)
    suspend fun deleteById(id: String): Result<Unit> = repository.deleteById(id)
}