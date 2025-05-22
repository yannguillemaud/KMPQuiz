package ygmd.kmpquiz

import io.ktor.client.HttpClient
import io.ktor.client.plugins.Charsets
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ygmd.kmpquiz.domain.repository.QandaRepository
import ygmd.kmpquiz.domain.repository.QandaRepositoryPersistenceImpl
import ygmd.kmpquiz.domain.useCase.GetSavedQandaUseCase
import ygmd.kmpquiz.domain.useCase.SaveQandaUseCase
import ygmd.kmpquiz.domain.useCase.fetch.FetchQandasUseCase
import ygmd.kmpquiz.viewModel.FetchQandasVModel
import ygmd.kmpquiz.viewModel.SaveQandasVModel

fun initKoin(appModule: Module): KoinApplication {
    return startKoin {
        modules(
            appModule,
            coreModule
        )
    }
}

val coreModule = module {
    single<QandaRepository> {
        QandaRepositoryPersistenceImpl()
    }

    /* FETCH */
    factory {
        FetchQandasUseCase(get())
    }
    factory {
        FetchQandasVModel(get())
    }

    /* SAVE, GETALL */
    factory {
        GetSavedQandaUseCase(get())
    }
    factory {
        SaveQandaUseCase(get())
    }
    factory {
        SaveQandasVModel(get(), get())
    }

    factory {
        httpClient()
    }
}

fun httpClient() = HttpClient {
    install(ContentNegotiation){
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    Charsets {
        responseCharsetFallback = io.ktor.utils.io.charsets.Charsets.UTF_8
    }
}
