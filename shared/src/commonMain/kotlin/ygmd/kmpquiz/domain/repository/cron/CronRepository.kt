package ygmd.kmpquiz.domain.repository.cron

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ygmd.kmpquiz.domain.cron.CronExpression

sealed interface CronOperationError {
    data object NotExists: CronOperationError
}

interface CronRepository {
    fun getCrons(): Flow<Map<Long, CronExpression>>

    suspend fun updateCron(
        qandaId: Long, cron: CronExpression
    )

    suspend fun removeCron(
        qandaId: Long
    ): Either<CronOperationError, Unit>
}

class CronRepositoryImpl: CronRepository {
    private val _crons = MutableStateFlow(mutableMapOf<Long, CronExpression>())

    override fun getCrons(): Flow<Map<Long, CronExpression>> {
        return _crons.asStateFlow()
    }

    override suspend fun updateCron(qandaId: Long, cron: CronExpression) {
        _crons.value = _crons.value.apply {
            qandaId to cron
        }
    }

    override suspend fun removeCron(
        qandaId: Long,
    ): Either<CronOperationError, Unit> {
        _crons.value = _crons.value.apply {
            remove(qandaId) ?: return CronOperationError.NotExists.left()
        }
        return Unit.right()
    }
}