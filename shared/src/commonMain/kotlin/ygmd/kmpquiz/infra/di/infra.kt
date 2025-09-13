package ygmd.kmpquiz.infra.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import ygmd.kmpquiz.data.database.createDatabase
import ygmd.kmpquiz.data.database.sqlDriverFactory
import ygmd.kmpquiz.data.service.QandaFetcher
import ygmd.kmpquiz.infra.localImage.CS2MapPositionsFetcher

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

    factory<QandaFetcher> {
//        OpenTriviaFetcher(
//            client = get(),
//        )
        CS2MapPositionsFetcher(httpClient = get())
    }

    single {
        sqlDriverFactory()
    }

    single {
        createDatabase(get())
    }
}