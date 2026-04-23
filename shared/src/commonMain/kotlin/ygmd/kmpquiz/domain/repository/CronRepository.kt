package ygmd.kmpquiz.domain.repository

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.model.cron.QuizCron

interface CronRepository {
    fun observeCrons(): Flow<List<QuizCron>>
    suspend fun countCrons(): Int
    suspend fun insertCron(
        id: String,
        name: String,
        expression: String
    )

    suspend fun getCronById(id: String): Result<QuizCron>
}