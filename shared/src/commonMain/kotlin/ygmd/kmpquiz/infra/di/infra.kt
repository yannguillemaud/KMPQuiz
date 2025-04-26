package ygmd.kmpquiz.infra.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
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
import ygmd.kmpquiz.infra.appVersionProvider.AppVersionUpdateChecker
import ygmd.kmpquiz.infra.cs2.CS2MapPositionsFetcher
import ygmd.kmpquiz.infra.openTrivia.OpenTriviaFetcher
import ygmd.kmpquiz.infra.scheduler.CommonTaskScheduler

val infraModule = module {
    single {
        HttpClient(CIO) { // Assurez-vous d'utiliser le moteur client approprié pour votre KMP setup
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
                        ignoreUnknownKeys = true // Important pour la robustesse
                    },
                    contentType = ContentType.Text.Plain
                )
            }
        }
    }

    single<Fetcher>(named("CS2MapPositions")) {
        CS2MapPositionsFetcher(httpClient = get())
    }

    single<Fetcher>(named("OpenTrivia")) {
        OpenTriviaFetcher(client = get())
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
    
    single {
        AppVersionUpdateChecker(
            httpClient = get(),
            appVersionProvider = get()
        )
    }
}