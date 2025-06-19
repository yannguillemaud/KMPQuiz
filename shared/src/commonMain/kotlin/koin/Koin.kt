package koin

import co.touchlab.kermit.Logger
import network.httpClientProvider
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
            presentationModule
        )
    }
}

val infraModule = module {
    single { Logger.withTag("SharedLogger") }
    factory { httpClientProvider() }
}

