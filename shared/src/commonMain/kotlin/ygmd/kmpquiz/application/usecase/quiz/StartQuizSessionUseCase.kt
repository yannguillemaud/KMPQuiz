package ygmd.kmpquiz.application.usecase.quiz

import co.touchlab.kermit.Logger
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.entities.quiz.QuizSession
import ygmd.kmpquiz.domain.error.DomainError
import ygmd.kmpquiz.domain.error.DomainError.QuizSessionError.EmptyQuizSession
import ygmd.kmpquiz.domain.repository.QandaRepository
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

private val logger = Logger.withTag("QuizUseCaseImpl")

class StartQuizSessionUseCase(
    private val repository: QandaRepository,
) {
    suspend fun start(quizSession: QuizSession): Result<QuizSession> {
        logger.i { "Starting Quiz ${quizSession.quizId}: ${quizSession.title} with ${quizSession.size} qandas" }

        if (quizSession.qandas.isEmpty())
            return failure(EmptyQuizSession("Cannot start quiz with no qanda"))
        val qandasIds = quizSession.qandas.map { it.id }
        if (qandasIds.isEmpty())
            return failure(EmptyQuizSession("Cannot start quiz with no qanda ids"))

        val qandas = mutableListOf<Qanda>()
        val notFoundIds = mutableListOf<Long>()
        for (id in qandasIds) {
            repository.findById(id).fold(
                onFailure = { notFoundIds.add(id) },
                onSuccess = { qandas.add(it) }
            )
        }

        if (qandas.isEmpty()) {
            return failure(DomainError.QuizSessionError.QandasNotFoundForSession("No valid questions found for quiz"))
        }

        if (notFoundIds.isNotEmpty()) {
            logger.w { "Some questions weren't found: $notFoundIds, continuing with: ${qandas.size} questions" }
        }

        return success(
            QuizSession(
                quizId = quizSession.quizId,
                title = quizSession.title,
                qandas = qandas,
            )
        )
    }
}