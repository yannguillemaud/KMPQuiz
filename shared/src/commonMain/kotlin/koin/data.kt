package koin

import org.koin.dsl.module
import ygmd.kmpquiz.data.repository.cron.CronRepositoryImpl
import ygmd.kmpquiz.data.repository.qanda.InMemoryQandaRepository
import ygmd.kmpquiz.domain.repository.CronRepository
import ygmd.kmpquiz.domain.repository.QandaRepository

// Data Layer - Repositories & DataSources
val dataModule = module {
    // Repositories
    single<QandaRepository> {
        InMemoryQandaRepository()
    }

    single<CronRepository> {
        CronRepositoryImpl(storage = get())
    }

//    single<NotificationRepository> {
//        NotificationRepository
//    }
}
