package ygmd.kmpquiz.core.usecase.qanda

import ygmd.kmpquiz.core.domain.qanda.Qanda
import ygmd.kmpquiz.core.repository.QandaRepository

class DeleteQandasUseCase(private val repository: QandaRepository) {
    suspend fun delete(qanda: Qanda): Result<Unit> = repository.deleteById(qanda.id)
    suspend fun deleteById(id: String): Result<Unit> = repository.deleteById(id)
}