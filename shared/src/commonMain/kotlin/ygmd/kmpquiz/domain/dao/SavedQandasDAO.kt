package ygmd.kmpquiz.domain.dao

import ygmd.kmpquiz.domain.pojo.InternalQanda


interface SavedQandasDAO {
    fun getAll(): List<InternalQanda>
    suspend fun findById(id: Long): InternalQanda?
    suspend fun insert(qanda: InternalQanda): Long
    suspend fun update(internalQanda: InternalQanda)
    suspend fun deleteById(id: Long)
    suspend fun deleteAll()
}