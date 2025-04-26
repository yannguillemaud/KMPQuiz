package ygmd.kmpquiz

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ygmd.kmpquiz.domain.QuizFetchPort
import ygmd.kmpquiz.domain.QuizRepository
import ygmd.kmpquiz.domain.QuizUseCase
import ygmd.kmpquiz.service.QuizService
import ygmd.kmpquiz.service.fetch.QuizFetchPortDummyImpl
import ygmd.kmpquiz.service.repository.QuizPersistenceRepository

fun initKoin(appModule: Module): KoinApplication {
    return startKoin {
        modules(
            appModule,
            coreModule
        )
    }
}

val coreModule = module {
    singleOf(::QuizPersistenceRepository){
        bind<QuizRepository>()
    }

    singleOf(::QuizFetchPortDummyImpl) {
        bind<QuizFetchPort>()
    }

    singleOf(::QuizService) { bind<QuizUseCase>() }
}