package ygmd.kmpquiz.data

import kotlinx.serialization.json.Json
import org.koin.dsl.module
import ygmd.kmpquiz.data.repository.category.CategoryRepositoryImpl
import ygmd.kmpquiz.data.repository.category.PersistenceCategoryDao
import ygmd.kmpquiz.data.repository.cron.CronRepositoryImpl
import ygmd.kmpquiz.data.repository.fetch.FetchRepository
import ygmd.kmpquiz.data.repository.qanda.PersistenceQandaDao
import ygmd.kmpquiz.data.repository.qanda.QandaRepositoryImpl
import ygmd.kmpquiz.data.repository.quiz.QuizRepositoryImpl
import ygmd.kmpquiz.data.repository.relation.QuizCategoryRelationDao
import ygmd.kmpquiz.data.repository.relation.RelationRepositoryImpl
import ygmd.kmpquiz.data.service.SimpleCronCalculator
import ygmd.kmpquiz.domain.dao.CategoryDao
import ygmd.kmpquiz.domain.dao.QandaDao
import ygmd.kmpquiz.domain.dao.RelationDao
import ygmd.kmpquiz.domain.repository.CategoryRepository
import ygmd.kmpquiz.domain.repository.CronRepository
import ygmd.kmpquiz.domain.repository.QandaRepository
import ygmd.kmpquiz.domain.repository.QuizRepository
import ygmd.kmpquiz.domain.repository.RelationRepository
import ygmd.kmpquiz.domain.service.CronExecutionCalculator
import ygmd.kmpquiz.domain.usecase.qanda.QandaEditRepository
import ygmd.kmpquiz.domain.usecase.quizSession.QuizSessionRepository
import ygmd.kmpquiz.domain.usecase.quizSession.QuizSessionRepositoryImpl

// Data Layer - Repositories & DataSources
val dataModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    // Repositories
    single<QandaRepository> {
        QandaRepositoryImpl(
            qandaDao = get(),
            json = get(),
        )
    }

    single<FetchRepository> {
        FetchRepository(fetchers = getAll())
    }

    single<QuizRepository> {
        QuizRepositoryImpl(
            database = get(),
        )
    }

    single<CronRepository> {
        CronRepositoryImpl(database = get())
    }

    single<RelationRepository> {
        RelationRepositoryImpl(
            relationDao = get()
        )
    }

    single<QuizSessionRepository>{
        QuizSessionRepositoryImpl()
    }

    single<FetchRepository> {
        FetchRepository(
            fetchers = getAll()
        )
    }

    single<CronExecutionCalculator>{
        SimpleCronCalculator()
    }

    single<QandaDao> {
        PersistenceQandaDao(database = get())
    }

    single<RelationDao> {
        QuizCategoryRelationDao(get())
    }

    single {
        QandaEditRepository()
    }

    single<CategoryDao> {
        PersistenceCategoryDao(get())
    }

    single<CategoryRepository>{
        CategoryRepositoryImpl(categoryDao = get())
    }
}
