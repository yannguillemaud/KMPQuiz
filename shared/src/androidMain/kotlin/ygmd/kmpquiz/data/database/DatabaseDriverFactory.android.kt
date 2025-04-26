package ygmd.kmpquiz.data.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope
import ygmd.kmpquiz.database.KMPQuizDatabase

actual fun Scope.sqlDriverFactory(): SqlDriver {
    return AndroidSqliteDriver(
        schema = KMPQuizDatabase.Schema,
        context = androidContext(),
        name = "${DatabaseConstants.databaseName}.db"
    )
}