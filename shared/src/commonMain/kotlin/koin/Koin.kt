package koin

import co.touchlab.kermit.Logger
import network.httpClientProvider
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import ygmd.kmpquiz.domain.fetch.OpenTriviaFetchQanda
import ygmd.kmpquiz.domain.repository.cron.CronRepository
import ygmd.kmpquiz.domain.repository.cron.CronRepositoryImpl
import ygmd.kmpquiz.domain.repository.qanda.InMemoryQandaRepository
import ygmd.kmpquiz.domain.repository.qanda.QandaRepository
import ygmd.kmpquiz.domain.usecase.DeleteQandasUseCase
import ygmd.kmpquiz.domain.usecase.DeleteQandasUseCaseImpl
import ygmd.kmpquiz.domain.usecase.SaveQandasUseCase
import ygmd.kmpquiz.domain.usecase.SaveQandasUseCaseImpl
import ygmd.kmpquiz.viewModel.fetch.FetchQandasViewModel
import ygmd.kmpquiz.viewModel.quiz.QuizViewModel
import ygmd.kmpquiz.viewModel.save.SavedQandasViewModel
import ygmd.kmpquiz.viewModel.settings.SettingsViewModel

fun initKoin(appModule: Module = module {}): KoinApplication {
    return startKoin {
        modules(
            appModule,
            coreModule,
            module {
                single { Logger.withTag("SharedLogger") }
            }
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
        OpenTriviaFetchQanda(
            client = get(),
            logger = get()
        )
    }

    /*VModels*/
    factory {
        FetchQandasViewModel(
            fetchQandasUseCase = get()
        )
    }
    factory {
        SavedQandasViewModel(
            qandaRepository = get()
        )
    }
    factory {
        SettingsViewModel(
            qandaRepository = get(), cronRepository = get()
        )
    }
    factory {
        QuizViewModel(
            logger = get(),
            qandaRepository = get()
        )
    }

    /* DATABASE */
    single<QandaRepository> {
        InMemoryQandaRepository()
    }
    single<CronRepository> {
        CronRepositoryImpl()
    }

    /* USE CASE */
    factory<SaveQandasUseCase> {
        SaveQandasUseCaseImpl(
            repository = get(),
            logger = get()
        )
    }
    factory<DeleteQandasUseCase> {
        DeleteQandasUseCaseImpl(
            repository = get(),
            logger = get()
        )
    }
}