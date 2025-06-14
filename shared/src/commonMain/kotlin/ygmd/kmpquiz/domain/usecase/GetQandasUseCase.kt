package ygmd.kmpquiz.domain.usecase

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.pojo.qanda.InternalQanda
import ygmd.kmpquiz.domain.repository.qanda.QandaRepository

interface GetQandasUseCase {
    fun execute(): Flow<List<InternalQanda>>
    suspend fun getByid(id: Long): Result<InternalQanda>
}

class GetQandasUseCaseImpl(
    private val repository: QandaRepository
): GetQandasUseCase {
    override fun execute(): Flow<List<InternalQanda>> = repository.getAll()

    override suspend fun getByid(id: Long): Result<InternalQanda> = repository.findById(id)
}