package ygmd.kmpquiz.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ygmd.kmpquiz.domain.pojo.InternalQanda
import ygmd.kmpquiz.domain.repository.QandaRepository

class InMemoryQandaRepository: QandaRepository {
    private val memoryQandas: MutableList<InternalQanda> = mutableListOf()

    override fun observeQandas(): Flow<List<InternalQanda>> {
        return flow {
            emit(memoryQandas.toList())
        }
    }

    override suspend fun getAll(): List<InternalQanda> {
        return memoryQandas.toList()
    }

    override suspend fun saveAll(qandas: List<InternalQanda>) {
        memoryQandas.addAll(qandas)
    }

    override suspend fun save(qanda: InternalQanda) {
        if(memoryQandas.any { it.id == qanda.id }) return
        else memoryQandas.add(qanda)
    }

    override suspend fun findById(id: Long): InternalQanda? {
        return memoryQandas.firstOrNull { it.id == id }
    }

    override suspend fun deleteById(id: Long): Boolean {
        return memoryQandas.removeIf { it.id == id }
    }

    override suspend fun deleteAll(id: Long): Boolean {
        return memoryQandas.removeAll { true }
    }
}