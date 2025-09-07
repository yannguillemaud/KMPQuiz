package ygmd.kmpquiz.data.database

import app.cash.sqldelight.db.SqlDriver
import ygmd.kmpquiz.database.KMPQuizDatabase

expect interface DatabaseDriverFactory {
    fun createDriver(): SqlDriver
    fun deleteDatabase()
}

fun createDatabase(
    driverFactory: DatabaseDriverFactory,
    isDev: Boolean = false
): KMPQuizDatabase {
    if(isDev) driverFactory.deleteDatabase()
    val driver = driverFactory.createDriver()
    return KMPQuizDatabase(driver)
}