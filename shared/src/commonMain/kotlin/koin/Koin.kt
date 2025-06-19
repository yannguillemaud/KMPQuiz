package koin

import co.touchlab.kermit.Logger
import network.httpClientProvider
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import ygmd.kmpquiz.data.repository.notification.NotificationConfigRepository
import ygmd.kmpquiz.data.repository.notification.NotificationConfigRepositoryImpl
import ygmd.kmpquiz.data.repository.notification.ScheduledNotificationRepository
import ygmd.kmpquiz.data.repository.notification.ScheduledNotificationRepositoryImpl
import ygmd.kmpquiz.data.repository.qanda.InMemoryQandaRepository
import ygmd.kmpquiz.data.repository.qanda.QandaRepository
import ygmd.kmpquiz.data.repository.service.FetchQanda
import ygmd.kmpquiz.data.repository.service.NotificationService
import ygmd.kmpquiz.data.repository.service.NotificationServiceImpl
import ygmd.kmpquiz.data.service.QandaSource
import ygmd.kmpquiz.domain.usecase.DeleteQandasUseCase
import ygmd.kmpquiz.domain.usecase.DeleteQandasUseCaseImpl
import ygmd.kmpquiz.domain.usecase.FetchQandasUseCase
import ygmd.kmpquiz.domain.usecase.GetQandasUseCase
import ygmd.kmpquiz.domain.usecase.GetQandasUseCaseImpl
import ygmd.kmpquiz.domain.usecase.NotificationUseCase
import ygmd.kmpquiz.domain.usecase.NotificationUseCaseImpl
import ygmd.kmpquiz.domain.usecase.QuizUseCase
import ygmd.kmpquiz.domain.usecase.QuizUseCaseImpl
import ygmd.kmpquiz.domain.usecase.SaveQandasUseCase
import ygmd.kmpquiz.domain.usecase.SaveQandasUseCaseImpl
import ygmd.kmpquiz.infra.openTrivia.OpenTriviaFetcher
import ygmd.kmpquiz.viewModel.NotificationTestViewModel
import ygmd.kmpquiz.viewModel.fetch.FetchQandasViewModel
import ygmd.kmpquiz.viewModel.quiz.QuizViewModel
import ygmd.kmpquiz.viewModel.save.SavedQandasViewModel
import ygmd.kmpquiz.viewModel.settings.NotificationSettingsViewModel
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

    single<NotificationConfigRepository> {
        NotificationConfigRepositoryImpl(logger = get())
    }

    single<ScheduledNotificationRepository> {
        ScheduledNotificationRepositoryImpl(logger = get())
    }

    single<NotificationService> {
        NotificationServiceImpl(
            scheduledRepo = get(),
            configRepo = get(),
            qandaRepo = get(),
            logger = get()
        )
    }

    factory<NotificationUseCase> {
        NotificationUseCaseImpl(
            notificationService = get()
        )
    }

    // Remote DataSources
    factory {
        OpenTriviaFetcher(
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
    factory<FetchQanda> {
        FetchQandasUseCase(
            fetchers = mapOf(
                QandaSource.OPEN_TRIVIA to get<OpenTriviaFetcher>()
                // todo image api
            )
        )
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
            getQandasUseCase = get(),
            notificationConfigRepository = get()
        )
    }

    factory {
        QuizViewModel(
            quizUseCase = get(),
            logger = get()
        )
    }

    factory {
        NotificationTestViewModel(
            notificationUseCase = get()
        )
    }

    factory {
        NotificationSettingsViewModel(
            configRepository = get(),
            getQandasUseCase = get(),
            notificationUseCase = get()
        )
    }
}