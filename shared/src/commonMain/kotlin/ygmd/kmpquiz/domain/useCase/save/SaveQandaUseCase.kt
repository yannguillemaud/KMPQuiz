package ygmd.kmpquiz.domain.useCase.save

import ygmd.kmpquiz.domain.pojo.InternalQanda
import ygmd.kmpquiz.domain.repository.QandaRepository

class SaveQandaUseCase(private val qandaRepository: QandaRepository){
    suspend fun saveQanda(qanda: InternalQanda) {
        qandaRepository.save(qanda)
    }

    suspend fun saveAllQandas(qandas: List<InternalQanda>) {
        qandaRepository.saveAll(qandas)
    }
}