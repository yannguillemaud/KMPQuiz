package ygmd.kmpquiz.infra.di

import app.cash.sqldelight.db.SqlDriver
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import ygmd.kmpquiz.core.service.fetcher.Fetcher
import ygmd.kmpquiz.data.database.createDatabase
import ygmd.kmpquiz.data.database.sqlDriverFactory
import ygmd.kmpquiz.database.KMPQuizDatabase
import ygmd.kmpquiz.infra.fetcher.cs2.CS2Fetcher

expect fun platformEngine(): HttpClientEngine

val infraModule = module {
    single {
        HttpClient(platformEngine()) {
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )

                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    },
                    contentType = ContentType.Text.Plain
                )
            }
        }
    }

    single<Fetcher> {
        CS2Fetcher(
            httpClient = get(),
            dispatcher = Dispatchers.IO
        )
    }

    single<SqlDriver> {
        sqlDriverFactory()
    }

    single<KMPQuizDatabase> {
        createDatabase(driver = get())
    }
}