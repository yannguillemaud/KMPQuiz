package ygmd.kmpquiz.infra.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ygmd.kmpquiz.data.database.createDatabase
import ygmd.kmpquiz.data.database.sqlDriverFactory
import ygmd.kmpquiz.domain.scheduler.TaskScheduler
import ygmd.kmpquiz.domain.service.Fetcher
import ygmd.kmpquiz.infra.cs2.CS2MapPositionsFetcher
import ygmd.kmpquiz.infra.scheduler.CommonTaskScheduler

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

    single<Fetcher>(named("CS2MapPositions")) {
        CS2MapPositionsFetcher(httpClient = get())
    }

    single {
        sqlDriverFactory()
    }

    single {
        createDatabase(get())
    }

    single<TaskScheduler> {
        CommonTaskScheduler(
            quizWorkManager = get(),
            schedulerDataStore = get(),
            cronExecutionCalculator = get(),
        )
    }
}