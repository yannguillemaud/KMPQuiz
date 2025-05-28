package ygmd.kmpquiz.koin

import org.koin.dsl.module
import ygmd.kmpquiz.db.QandaRepositoryPersistenceImpl
import ygmd.kmpquiz.domain.repository.SavedQandaRepository

val serverModule = module {
    single<SavedQandaRepository> {
        QandaRepositoryPersistenceImpl()
    }
}