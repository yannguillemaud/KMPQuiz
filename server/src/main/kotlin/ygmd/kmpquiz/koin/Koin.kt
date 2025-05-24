package ygmd.kmpquiz.koin

import org.koin.dsl.module
import ygmd.kmpquiz.db.QandaRepositoryPersistenceImpl
import ygmd.kmpquiz.domain.repository.QandaRepository

val serverModule = module {
    single<QandaRepository> {
        QandaRepositoryPersistenceImpl()
    }
}