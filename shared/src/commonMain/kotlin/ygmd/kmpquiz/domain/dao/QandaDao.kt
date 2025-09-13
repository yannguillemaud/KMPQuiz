package ygmd.kmpquiz.domain.dao

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.repository.DraftQanda

interface QandaDao {
    fun observeQandas(): Flow<List<Qanda>>

    fun getAllQandas(): List<Qanda>
    fun getQandaByContextKey(contextKey: String): Qanda?
    fun getQandaById(id: String): Qanda?
    fun getQandasByCategory(category: String): List<Qanda>

    fun saveDraft(draftQanda: DraftQanda): String
    fun saveAllDraft(draftQandas: List<DraftQanda>)

    fun updateQanda(qandaId: String, qanda: Qanda)

    fun deleteAllQandas()
    fun deleteQandaById(id: String)
}