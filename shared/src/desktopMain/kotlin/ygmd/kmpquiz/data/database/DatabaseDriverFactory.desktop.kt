package ygmd.kmpquiz.data.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.koin.core.scope.Scope
import ygmd.kmpquiz.database.KMPQuizDatabase
import java.io.File
import java.util.Properties

actual fun Scope.sqlDriverFactory(): SqlDriver {
    val appDataDir = File(System.getenv("APPDATA") ?: System.getProperty("user.home"), "KMPQuiz")
    appDataDir.mkdirs()
    val databaseFile = File(appDataDir, "${DatabaseConstants.DATABASE_NAME}.db")

    return JdbcSqliteDriver(
        url = "jdbc:sqlite:${databaseFile.absolutePath}",
        properties = Properties().apply { put("foreign_keys", "true") },
        schema = KMPQuizDatabase.Schema,
    )
}
