package ygmd.kmpquiz.domain.usecase

import co.touchlab.kermit.Logger
import ygmd.kmpquiz.domain.error.DomainError.QandaError.NotFound
import ygmd.kmpquiz.domain.error.DomainError.QandaError.ValidationError
import ygmd.kmpquiz.domain.pojo.InternalQanda
import ygmd.kmpquiz.domain.pojo.QuizSession
import ygmd.kmpquiz.domain.repository.qanda.QandaRepository
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

interface QuizUseCase {
    suspend fun start(qandasIds: List<Long>): Result<QuizSession>
}

class QuizUseCaseImpl(
    private val repository: QandaRepository,
    private val logger: Logger,
) : QuizUseCase {
    override suspend fun start(qandasIds: List<Long>): Result<QuizSession> {
        logger.i { "Starting Quiz with ${qandasIds.size} questions" }
        if (qandasIds.isEmpty())
            return failure(
                ValidationError(
                    field = "qandaIds",
                    reason = "Cannot start quiz with empty ids"
                )
            )

        val qandas = mutableListOf<InternalQanda>()
        val notFoundIds = mutableListOf<Long>()

        for (id in qandasIds) {
            repository.findById(id).fold(
                onFailure = { notFoundIds.add(id) },
                onSuccess = { qandas.add(it) }
            )
        }

        return when {
            qandas.isEmpty() -> {
                logger.e { "No valid questions found for quiz" }
                failure(NotFound)
            }
            notFoundIds.isNotEmpty() -> {
                logger.w { "Some questions weren't found: $notFoundIds, continuing with: ${qandas.size} questions" }
                success(QuizSession(qandas.shuffled()))
            }
            else -> {
                logger.i { "Quiz started with ${qandas.size} questions" }
                success(QuizSession(qandas.shuffled()))
            }
        }
    }
}