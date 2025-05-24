package ygmd.kmpquiz.domain.useCase.get

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.pojo.InternalQanda
import ygmd.kmpquiz.domain.repository.QandaRepository

class GetQandaUseCase(private val qandaRepository: QandaRepository){
    fun getAll(): Flow<List<InternalQanda>> = qandaRepository.observeQandas()
    suspend fun find(id: Long): InternalQanda? = qandaRepository.findById(id)
}
