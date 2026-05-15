package ygmd.kmpquiz.data.database

import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope
import ygmd.kmpquiz.database.KMPQuizDatabase

actual fun Scope.sqlDriverFactory(): SqlDriver {
    return AndroidSqliteDriver(
        schema = KMPQuizDatabase.Schema,
        context = androidContext(),
        name = "${DatabaseConstants.databaseName}.db",
        callback = object : AndroidSqliteDriver.Callback(KMPQuizDatabase.Schema) {
            override fun onConfigure(db: SupportSQLiteDatabase) {
                super.onConfigure(db)
                db.setForeignKeyConstraintsEnabled(true)
            }
        }
    )
}