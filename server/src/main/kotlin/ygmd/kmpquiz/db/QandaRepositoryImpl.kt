package kotlin.ygmd.kmpquiz.db

import ygmd.kmpquiz.domain.pojo.QANDA
import ygmd.kmpquiz.domain.repository.QandaRepository

class QandaRepositoryPersistenceImpl: QandaRepository {
    private val _qandas: MutableList<QANDA> = mutableListOf()

    override fun observeQandas(): Flow<List<QANDA>> {
        return _qandas.asStateFlow()
    }

    override fun exists(qanda: QANDA): Boolean = _qandas.value.any { it == qanda }

    override fun saveAll(qandas: List<QANDA>) {
        this._qandas.update { _ -> qandas }
        saveCallback()
    }

    override fun save(qanda: QANDA) {
        this._qandas.update { it + qanda }
        saveCallback()
    }

    private fun saveCallback() = println("QandasRepository now contains ${_qandas.value}")
}
