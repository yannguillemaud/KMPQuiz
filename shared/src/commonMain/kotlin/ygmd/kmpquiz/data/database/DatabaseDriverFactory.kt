package ygmd.kmpquiz.data.database

import app.cash.sqldelight.db.SqlDriver
import org.koin.core.scope.Scope
import ygmd.kmpquiz.database.KMPQuizDatabase

expect fun Scope.sqlDriverFactory(): SqlDriver

fun createDatabase(
    driver: SqlDriver,
): KMPQuizDatabase = KMPQuizDatabase(driver)