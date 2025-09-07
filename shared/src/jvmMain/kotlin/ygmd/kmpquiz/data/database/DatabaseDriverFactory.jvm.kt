package ygmd.kmpquiz.data.database

import app.cash.sqldelight.db.SqlDriver

actual interface DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver
    actual fun deleteDatabase()
}