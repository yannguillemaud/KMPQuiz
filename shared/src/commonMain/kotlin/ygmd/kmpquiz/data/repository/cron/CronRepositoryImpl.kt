package ygmd.kmpquiz.data.repository.cron

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ygmd.kmpquiz.database.KMPQuizDatabase
import ygmd.kmpquiz.domain.model.cron.QuizCron
import ygmd.kmpquiz.domain.repository.CronRepository

private val logger = Logger.withTag("CronRepository")

class CronRepositoryImpl(private val database: KMPQuizDatabase) : CronRepository {
    override fun observeCrons(): Flow<List<QuizCron>> {
        return database.quizCronQueries.getAll().asFlow().mapToList(Dispatchers.IO)
            .map { cronsEntity ->
                cronsEntity.map {
                    QuizCron(
                        id = it.id,
                        name = it.name,
                        expression = it.expression,
                    )
                }
            }
    }

    override suspend fun countCrons(): Int {
        return database.quizCronQueries.countCrons().executeAsOneOrNull()?.toInt() ?: 0
    }

    override suspend fun insertCron(
        id: String,
        name: String,
        expression: String
    ) {
        database.quizCronQueries.insertCron(id, name, expression)
    }

    override suspend fun getCronById(id: String): Result<QuizCron> {
        val cron = database.quizCronQueries.getById(id).executeAsOneOrNull()
            ?: return Result.failure(Exception("Cron not found"))
        return Result.success(
            QuizCron(
                id = cron.id,
                name = cron.name,
                expression = cron.expression
            )
        )
    }
}