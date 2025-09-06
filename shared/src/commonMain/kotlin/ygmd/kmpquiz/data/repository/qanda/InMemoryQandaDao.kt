package ygmd.kmpquiz.data.repository.qanda

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.repository.DraftQanda
import java.util.UUID

private val logger = Logger.withTag(QandaRepositoryImpl::class.simpleName.toString())

class InMemoryQandaDao: QandaDao {
    private val _qandasMap = MutableStateFlow<Map<String, Qanda>>(emptyMap())
    private val qandasMap: Map<String, Qanda>
        get() = _qandasMap.value

    override fun observeQandas(): Flow<List<Qanda>> = _qandasMap.map { it.values.toList() }

    override fun getAllQandas(): List<Qanda> {
        return _qandasMap.value.values.toList()
    }

    override fun getQandaByContextKey(contextKey: String): Qanda? {
        return _qandasMap.value.values.firstOrNull {
            it.contextKey == contextKey
        }
    }

    override fun getQandasByCategory(category: String): List<Qanda> {
        return _qandasMap.value.values.filter { it.metadata.category == category }
    }

    override fun getQandaById(id: String): Qanda? {
        return _qandasMap.value[id]
    }

    override fun saveDraft(draftQanda: DraftQanda): String {
        val newId = UUID.randomUUID().toString()
        val saved = draftQanda.toQanda(newId)
        _qandasMap.update { it + (newId to saved) }
        return newId
    }

    override fun saveAllDraft(qandas: List<DraftQanda>) {
        val toAdd = qandas
                .map { draft -> draft.toQanda() }
                .associateBy { it.id }

        _qandasMap.update { it + toAdd }
    }

    override fun updateQanda(qandaId: String, qanda: Qanda) {
        if(getQandaById(qandaId) == null) throw IllegalStateException("Qanda $qandaId not found")
        _qandasMap.value += (qanda.id to qanda)
    }


    override fun deleteAllQandas() {
        _qandasMap.value = emptyMap()
    }

    override fun deleteQandaById(id: String) {
        if(getQandaById(id) == null) throw IllegalStateException("Qanda $id not found")
        _qandasMap.value -= id
    }
}