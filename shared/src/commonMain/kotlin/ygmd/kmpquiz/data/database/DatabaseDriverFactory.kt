package ygmd.kmpquiz.data.database

import app.cash.sqldelight.db.SqlDriver
import ygmd.kmpquiz.database.KMPQuizDatabase

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DatabaseDriverFactory): KMPQuizDatabase {
    val driver = driverFactory.createDriver()
    return KMPQuizDatabase(driver)
}