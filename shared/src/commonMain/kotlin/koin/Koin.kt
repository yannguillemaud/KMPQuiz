package koin

import network.httpClientProvider
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import ygmd.kmpquiz.domain.repository.InMemoryQandaRepository
import ygmd.kmpquiz.domain.repository.QandaRepository
import ygmd.kmpquiz.domain.useCase.fetch.OpenTriviaFetchQanda
import ygmd.kmpquiz.viewModel.GetQandasVModel
import ygmd.kmpquiz.viewModel.fetch.FetchQandasVModel
import ygmd.kmpquiz.viewModel.fetch.SaveQandasVModel

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

    /*VModels*/
//    factory {
//        GetQandasVModel(get())
//    }
    factory {
        FetchQandasVModel(
            fetchQandasUseCase = get(),
            qandaRepository = get(),
        )
    }
    factory {
        SaveQandasVModel(get())
    }

    /* DATABASE */
    single<QandaRepository> {
        InMemoryQandaRepository()
    }
}