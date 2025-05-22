package ygmd.kmpquiz.db

import com.zaxxer.hikari.HikariConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://localhost:5432/tondb"
            driverClassName = "org.postgresql.Driver"
            username = "postgres"
            password = "motdepasse"
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        Database.connect(HikariDataSource(config))

        // Optionnel : création automatique des tables
        transaction {
            // SchemaUtils.create(UserTable)
        }
    }
}