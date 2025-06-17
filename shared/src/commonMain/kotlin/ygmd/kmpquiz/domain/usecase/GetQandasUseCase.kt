package ygmd.kmpquiz.domain.usecase

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.data.repository.qanda.QandaRepository
import ygmd.kmpquiz.domain.entities.qanda.Qanda

interface GetQandasUseCase {
    fun execute(): Flow<List<Qanda>>
    suspend fun getByid(id: Long): Result<Qanda>
}

class GetQandasUseCaseImpl(
    private val repository: QandaRepository
): GetQandasUseCase {
    override fun execute(): Flow<List<Qanda>> = repository.getAll()

    override suspend fun getByid(id: Long): Result<Qanda> = repository.findById(id)
}