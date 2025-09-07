package ygmd.kmpquiz.data.database

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import ygmd.kmpquiz.database.KMPQuizDatabase

actual interface DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver
    actual fun deleteDatabase()
}

class AndroidDatabaseDriverFactory(private val context: Context) : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = KMPQuizDatabase.Schema,
            context = context,
            name = "kmpquiz.db",
            callback = object : AndroidSqliteDriver.Callback(KMPQuizDatabase.Schema) {
                override fun onUpgrade(
                    db: SupportSQLiteDatabase,
                    oldVersion: Int,
                    newVersion: Int
                ) {
                    if (oldVersion < newVersion) {
                        context.deleteDatabase("kmpquiz.db")
                    }
                }
            }
        )
    }

    override fun deleteDatabase() {
        context.deleteDatabase("kmpquiz.db")
    }
}

object DatabaseProvider {
    private var driverInstance: SqlDriver? = null
    private var databaseInstance: KMPQuizDatabase? = null
    private lateinit var factory: DatabaseDriverFactory // Sera initialisée

    // Doit être appelé une fois au démarrage de l'application
    fun initialize(factory: DatabaseDriverFactory) {
        this.factory = factory
    }

    @Synchronized // Important pour la sécurité des threads
    fun getDriver(): SqlDriver {
        if (driverInstance == null) {
            driverInstance = factory.createDriver()
        }
        return driverInstance!!
    }

    @Synchronized
    fun getDatabase(): KMPQuizDatabase {
        if (databaseInstance == null) {
            databaseInstance = KMPQuizDatabase(getDriver())
        }
        return databaseInstance!!
    }

    @Synchronized
    fun resetDatabase() {
        // Fermer les connexions existantes si le driver le permet
        driverInstance?.close() // SqlDriver a une méthode close()
        driverInstance = null
        databaseInstance = null
        // Supprimer physiquement la base de données
        factory.deleteDatabase()
        // La prochaine fois que getDriver() ou getDatabase() sera appelé,
        // une nouvelle instance sera créée.
    }
}