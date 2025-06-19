package koin

import co.touchlab.kermit.Logger
import org.koin.dsl.module
import ygmd.kmpquiz.data.repository.notification.NotificationConfigRepository
import ygmd.kmpquiz.data.repository.notification.NotificationConfigRepositoryImpl
import ygmd.kmpquiz.data.repository.notification.ScheduledNotificationRepository
import ygmd.kmpquiz.data.repository.notification.ScheduledNotificationRepositoryImpl
import ygmd.kmpquiz.data.repository.qanda.InMemoryQandaRepository
import ygmd.kmpquiz.data.repository.qanda.QandaRepository
import ygmd.kmpquiz.data.repository.service.NotificationService
import ygmd.kmpquiz.data.repository.service.NotificationServiceImpl
import ygmd.kmpquiz.data.service.QandaFetcher
import ygmd.kmpquiz.domain.usecase.NotificationUseCase
import ygmd.kmpquiz.domain.usecase.NotificationUseCaseImpl
import ygmd.kmpquiz.infra.openTrivia.OpenTriviaFetcher

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
    factory<QandaFetcher> {
        OpenTriviaFetcher(
            client = get(),
            logger = Logger.withTag("Open trivia fetcher")
        )
    }
}
