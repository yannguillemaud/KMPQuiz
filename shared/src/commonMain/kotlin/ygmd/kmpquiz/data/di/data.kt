package ygmd.kmpquiz.data.di

import kotlinx.serialization.json.Json
import org.koin.dsl.module
import ygmd.kmpquiz.data.repository.category.CategoryRepositoryImpl
import ygmd.kmpquiz.data.repository.category.PersistenceCategoryDao
import ygmd.kmpquiz.data.repository.fetch.FetchRepository
import ygmd.kmpquiz.data.repository.qanda.PersistenceQandaDao
import ygmd.kmpquiz.data.repository.qanda.QandaRepositoryImpl
import ygmd.kmpquiz.data.repository.quiz.PermissionRepositoryImpl
import ygmd.kmpquiz.data.repository.quiz.QuizRepositoryImpl
import ygmd.kmpquiz.data.repository.relation.QuizCategoryRelationDao
import ygmd.kmpquiz.data.repository.relation.RelationRepositoryImpl
import ygmd.kmpquiz.data.repository.scheduler.SchedulerDataStoreImpl
import ygmd.kmpquiz.domain.dao.CategoryDao
import ygmd.kmpquiz.domain.dao.QandaDao
import ygmd.kmpquiz.domain.dao.RelationDao
import ygmd.kmpquiz.domain.repository.CategoryRepository
import ygmd.kmpquiz.domain.repository.PermissionRepository
import ygmd.kmpquiz.domain.repository.QandaRepository
import ygmd.kmpquiz.domain.repository.QuizRepository
import ygmd.kmpquiz.domain.repository.RelationRepository
import ygmd.kmpquiz.domain.repository.SchedulerDataStore
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

    single<SchedulerDataStore> {
        SchedulerDataStoreImpl(
            dataStore = get(),
            json = get()
        )
    }

    single<PermissionRepository> {
        PermissionRepositoryImpl(grantManager = get())
    }
}
