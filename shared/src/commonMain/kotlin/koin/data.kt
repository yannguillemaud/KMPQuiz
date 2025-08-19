package koin

import org.koin.dsl.module
import ygmd.kmpquiz.data.repository.QuizRepository.QuizRepositoryImpl
import ygmd.kmpquiz.data.repository.fetch.FetchRepositoryImpl
import ygmd.kmpquiz.data.repository.qanda.InMemoryQandaRepository
import ygmd.kmpquiz.data.service.SimpleCronCalculator
import ygmd.kmpquiz.domain.repository.FetchRepository
import ygmd.kmpquiz.domain.repository.QandaRepository
import ygmd.kmpquiz.domain.repository.QuizRepository
import ygmd.kmpquiz.domain.service.CronExecutionCalculator

// Data Layer - Repositories & DataSources
val dataModule = module {
    // Repositories
    single<QandaRepository> {
        InMemoryQandaRepository()
    }

    single<QuizRepository> {
        QuizRepositoryImpl()
    }

    single<FetchRepository> {
        FetchRepositoryImpl()
    }

    single<CronExecutionCalculator>{
        SimpleCronCalculator()
    }
}
