package ygmd.kmpquiz.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.qualifier._q
import ygmd.kmpquiz.domain.pojo.QANDA

interface QandaRepository {
    fun observeQandas(): Flow<List<QANDA>>
    fun saveAll(qandas: List<QANDA>)
    fun save(qanda: QANDA)
    fun exists(qanda: QANDA): Boolean
}

class QandaRepositoryPersistenceImpl: QandaRepository {
    private val _qandas = MutableStateFlow<List<QANDA>>(emptyList())

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
