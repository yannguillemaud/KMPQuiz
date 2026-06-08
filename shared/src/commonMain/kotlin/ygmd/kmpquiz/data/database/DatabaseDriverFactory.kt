package ygmd.kmpquiz.data.database

import app.cash.sqldelight.db.SqlDriver
import org.koin.core.scope.Scope
import ygmd.kmpquiz.data.database.DatabaseAdapters.quizAdapter
import ygmd.kmpquiz.database.KMPQuizDatabase

expect fun Scope.sqlDriverFactory(): SqlDriver

fun createDatabase(
    driver: SqlDriver,
): KMPQuizDatabase = KMPQuizDatabase(
    driver = driver,
    qanda_entityAdapter = DatabaseAdapters.qandaAdapter,
    quiz_entityAdapter = quizAdapter,
    quiz_scheduler_configuration_entityAdapter = DatabaseAdapters.quizSchedulerConfigurationAdapter,
    quiz_sessionAdapter = DatabaseAdapters.quizSessionAdapter,
)