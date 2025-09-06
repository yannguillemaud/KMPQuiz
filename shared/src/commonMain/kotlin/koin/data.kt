package koin

import org.koin.dsl.module
import ygmd.kmpquiz.data.repository.fetch.FetchRepositoryImpl
import ygmd.kmpquiz.data.repository.qanda.PersistenceQandaDao
import ygmd.kmpquiz.data.repository.qanda.QandaDao
import ygmd.kmpquiz.data.repository.qanda.QandaRepositoryImpl
import ygmd.kmpquiz.data.repository.quiz.PersistenceQuizDao
import ygmd.kmpquiz.data.repository.quiz.PersistenceQuizRepository
import ygmd.kmpquiz.data.repository.quiz.QuizDao
import ygmd.kmpquiz.data.service.SimpleCronCalculator
import ygmd.kmpquiz.domain.repository.FetchRepository
import ygmd.kmpquiz.domain.repository.QandaRepository
import ygmd.kmpquiz.domain.repository.QuizRepository
import ygmd.kmpquiz.domain.service.CronExecutionCalculator

// Data Layer - Repositories & DataSources
val dataModule = module {
    // Repositories
    single<QandaRepository> {
        QandaRepositoryImpl(qandaDao = get())
    }

    single<QuizRepository> {
        PersistenceQuizRepository(get())
    }

    single<FetchRepository> {
        FetchRepositoryImpl()
    }

    single<CronExecutionCalculator>{
        SimpleCronCalculator()
    }

    single<QandaDao> {
        PersistenceQandaDao(get())
    }

    single<QuizDao> {
        PersistenceQuizDao(get())
    }
}
