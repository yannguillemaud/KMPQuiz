package ygmd.kmpquiz.data.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import ygmd.kmpquiz.database.KMPQuizDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = KMPQuizDatabase.Schema,
            context = context,
            name = "kmpquiz.db"
        )
    }
}