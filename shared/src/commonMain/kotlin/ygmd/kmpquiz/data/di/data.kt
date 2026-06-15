package ygmd.kmpquiz.data.di

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import ygmd.kmpquiz.core.repository.CategoryRepository
import ygmd.kmpquiz.core.repository.FetcherRepository
import ygmd.kmpquiz.core.repository.PermissionRepository
import ygmd.kmpquiz.core.repository.QandaRepository
import ygmd.kmpquiz.core.repository.QuizRepository
import ygmd.kmpquiz.core.repository.SessionRepository
import ygmd.kmpquiz.data.dao.CategoryDao
import ygmd.kmpquiz.data.dao.QandaDao
import ygmd.kmpquiz.data.repository.category.PersistentCategoryRepository
import ygmd.kmpquiz.data.repository.category.SQLDelightCategoryDao
import ygmd.kmpquiz.data.repository.fetcher.InMemoryFetcherRepository
import ygmd.kmpquiz.data.repository.permission.GrantPermissionRepository
import ygmd.kmpquiz.data.repository.qanda.PersistentQandaRepository
import ygmd.kmpquiz.data.repository.qanda.SQLDelightQandaDao
import ygmd.kmpquiz.data.repository.quiz.PersistentQuizRepository
import ygmd.kmpquiz.data.repository.session.PersistentSessionRepository

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
        PersistentQandaRepository(
            qandaDao = get(),
        )
    }

    single<QuizRepository> {
        PersistentQuizRepository(
            database = get(),
        )
    }

    single<SessionRepository>{
        PersistentSessionRepository(
            database = get(),
            dispatcher = Dispatchers.IO
        )
    }

    single<QandaDao> {
        SQLDelightQandaDao(database = get())
    }

    single<CategoryDao> {
        SQLDelightCategoryDao(get())
    }

    single<CategoryRepository>{
        PersistentCategoryRepository(categoryDao = get())
    }

    single<PermissionRepository> {
        GrantPermissionRepository(grantManager = get())
    }

    single<FetcherRepository>{
        InMemoryFetcherRepository(
            fetchers = getAll()
        )
    }
}
