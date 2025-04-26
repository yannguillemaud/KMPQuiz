package ygmd.kmpquiz.data.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.koin.core.scope.Scope

actual fun Scope.sqlDriverFactory(): SqlDriver {
    val driver = JdbcSqliteDriver(url = JdbcSqliteDriver.IN_MEMORY)
    KMPQuizDatabase.Schema.create(driver)
    return driver
}