package koin

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

fun initKoin(appModule: Module = module {}): KoinApplication {
    return startKoin {
        modules(
            appModule,
            infraModule,
            dataModule,
            domainModule,
            viewModelModule,
        )
    }
}

