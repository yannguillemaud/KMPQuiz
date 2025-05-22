package ygmd.kmpquiz.domain.useCase

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.pojo.QANDA
import ygmd.kmpquiz.domain.repository.QandaRepository

class SaveQandaUseCase(private val qandaRepository: QandaRepository){
    fun saveQanda(qanda: QANDA) {
        qandaRepository.save(qanda)
    }

    fun saveAllQandas(qandas: List<QANDA>) {
        qandaRepository.saveAll(qandas)
    }
}

class GetSavedQandaUseCase(private val qandaRepository: QandaRepository){
    operator fun invoke(): Flow<List<QANDA>> = qandaRepository.observeQandas()
    fun exists(qanda: QANDA): Boolean = qandaRepository.exists(qanda)
}
