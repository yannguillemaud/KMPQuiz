package ygmd.kmpquiz.data.di

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import ygmd.kmpquiz.data.repository.category.CategoryRepositoryImpl
import ygmd.kmpquiz.data.repository.category.PersistenceCategoryDao
import ygmd.kmpquiz.data.repository.fetch.FetchRepository
import ygmd.kmpquiz.data.repository.qanda.InMemoryQandaDao
import ygmd.kmpquiz.data.repository.qanda.QandaRepositoryImpl
import ygmd.kmpquiz.data.repository.quiz.PermissionRepositoryImpl
import ygmd.kmpquiz.data.repository.quiz.QuizRepositoryImpl
import ygmd.kmpquiz.data.repository.relation.QuizCategoryRelationDao
import ygmd.kmpquiz.data.repository.relation.RelationRepositoryImpl
import ygmd.kmpquiz.data.repository.scheduler.SchedulerDataStoreImpl
import ygmd.kmpquiz.data.repository.session.PersistentSessionRepository
import ygmd.kmpquiz.domain.dao.CategoryDao
import ygmd.kmpquiz.domain.dao.QandaDao
import ygmd.kmpquiz.domain.dao.RelationDao
import ygmd.kmpquiz.domain.repository.CategoryRepository
import ygmd.kmpquiz.domain.repository.PermissionRepository
import ygmd.kmpquiz.domain.repository.QandaRepository
import ygmd.kmpquiz.domain.repository.QuizRepository
import ygmd.kmpquiz.domain.repository.RelationRepository
import ygmd.kmpquiz.domain.repository.SchedulerDataStore
import ygmd.kmpquiz.domain.repository.SessionRepository

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

    single<SessionRepository>{
        PersistentSessionRepository(
            database = get(),
            dispatcher = Dispatchers.IO
        )
    }

    single<FetchRepository> {
        FetchRepository(
            fetchers = getAll()
        )
    }

    single<QandaDao> {
        InMemoryQandaDao(database = get())
    }

    single<RelationDao> {
        QuizCategoryRelationDao(get())
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
