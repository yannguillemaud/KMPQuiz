package ygmd.kmpquiz.domain.dao

import ygmd.kmpquiz.domain.pojo.InternalQanda
import ygmd.kmpquiz.domain.repository.QandaRepository

// TODO - use with SQLDelight
class SavedQandasDAOImpl(
    val repository: QandaRepository
): SavedQandasDAO {
    override fun getAll(): List<InternalQanda> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: Long): InternalQanda? {
        TODO("Not yet implemented")
    }

    override suspend fun insert(qanda: InternalQanda): Long {
        TODO("Not yet implemented")
    }

    override suspend fun update(internalQanda: InternalQanda) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteById(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAll() {
        TODO("Not yet implemented")
    }
}