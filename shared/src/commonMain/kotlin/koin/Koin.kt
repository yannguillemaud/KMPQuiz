package koin

import co.touchlab.kermit.Logger
import network.httpClientProvider
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import ygmd.kmpquiz.domain.repository.cron.CronRepository
import ygmd.kmpquiz.domain.repository.cron.CronRepositoryImpl
import ygmd.kmpquiz.domain.repository.qanda.InMemoryQandaRepository
import ygmd.kmpquiz.domain.repository.qanda.QandaRepository
import ygmd.kmpquiz.domain.usecase.DeleteQandasUseCase
import ygmd.kmpquiz.domain.usecase.DeleteQandasUseCaseImpl
import ygmd.kmpquiz.domain.usecase.FetchQandaUseCase
import ygmd.kmpquiz.domain.usecase.GetQandasUseCase
import ygmd.kmpquiz.domain.usecase.GetQandasUseCaseImpl
import ygmd.kmpquiz.domain.usecase.OpenTriviaFetchQanda
import ygmd.kmpquiz.domain.usecase.QuizUseCase
import ygmd.kmpquiz.domain.usecase.QuizUseCaseImpl
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
            dataModule,
            domainModule,
            presentationModule
        )
    }
}

// Core - Infrastructure
val coreModule = module {
    single { Logger.withTag("SharedLogger") }
    factory { httpClientProvider() }
}

// Data Layer - Repositories & DataSources
val dataModule = module {
    // Repositories
    single<QandaRepository> {
        InMemoryQandaRepository()
    }

    single<CronRepository> {
        CronRepositoryImpl()
    }

    // Remote DataSources
    factory {
        OpenTriviaFetchQanda(
            client = get(),
            logger = get()
        )
    }
}

// Domain Layer - Use Cases
val domainModule = module {
    // Qanda Use Cases
    factory<GetQandasUseCase> {
        GetQandasUseCaseImpl(
            repository = get()
        )
    }

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

    // Quiz Use Cases
    factory<QuizUseCase> {
        QuizUseCaseImpl(
            repository = get(),
            logger = get()
        )
    }

    // Fetch Use Cases
    factory<FetchQandaUseCase> {
        get<OpenTriviaFetchQanda>()
    }
}

// Presentation Layer - ViewModels
val presentationModule = module {
    factory {
        FetchQandasViewModel(
            fetchQandaUseCase = get(),
            getQandasUseCase = get()
        )
    }

    factory {
        SavedQandasViewModel(
            getQandasUseCase = get(),
            saveQandaUseCase = get(),
            deleteQandasUseCase = get()
        )
    }

    factory {
        SettingsViewModel(
            qandaRepository = get(),
            cronRepository = get()
        )
    }

    factory {
        QuizViewModel(
            quizUseCase = get(),
            logger = get()
        )
    }
}