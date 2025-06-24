package ygmd.kmpquiz.application.usecase.qanda

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.repository.QandaRepository

interface GetQandasUseCase {
    fun observeAll(): Flow<List<Qanda>>
    suspend fun getAll(): List<Qanda>
    suspend fun getByid(id: Long): Result<Qanda>
}

class GetQandasUseCaseImpl(
    private val repository: QandaRepository
): GetQandasUseCase {
    override fun observeAll(): Flow<List<Qanda>> = repository.observeAll()

    override suspend fun getAll(): List<Qanda> = repository.getAll()

    override suspend fun getByid(id: Long): Result<Qanda> = repository.findById(id)
}