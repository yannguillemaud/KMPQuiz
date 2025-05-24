package koin

import network.httpClientProvider
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import ygmd.kmpquiz.domain.repository.QandaRepository
import ygmd.kmpquiz.domain.useCase.fetch.OpenTriviaFetchQanda
import ygmd.kmpquiz.domain.useCase.get.GetQandaUseCase
import ygmd.kmpquiz.domain.useCase.save.SaveQandaUseCase
import ygmd.kmpquiz.impl.InMemoryQandaRepository
import ygmd.kmpquiz.viewModel.FetchQandasVModel
import ygmd.kmpquiz.viewModel.GetQandasVModel

fun initKoin(appModule: Module = module {}): KoinApplication {
    return startKoin {
        modules(
            appModule,
            coreModule,
        )
    }
}

val coreModule = module {
    /*HTTP Provider*/
    factory {
        httpClientProvider()
    }

    /*USE CASES*/
    factory {
        OpenTriviaFetchQanda(get())
    }
    factory {
        GetQandaUseCase(get())
    }
    factory {
        SaveQandaUseCase(get())
    }

    /*VModels*/
    factory {
        GetQandasVModel(get())
    }
    factory {
        FetchQandasVModel(get())
    }

    /* DATABASE */
    single<QandaRepository> {
        InMemoryQandaRepository()
    }
}