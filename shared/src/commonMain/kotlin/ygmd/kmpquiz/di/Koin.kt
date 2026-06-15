package ygmd.kmpquiz.di

import dev.brewkits.grant.di.grantModule
import dev.brewkits.grant.di.grantPlatformModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import ygmd.kmpquiz.data.di.dataModule
import ygmd.kmpquiz.core.di.domainModule
import ygmd.kmpquiz.presentation.viewModel.viewModelModule
import ygmd.kmpquiz.infra.di.infraModule

expect val platformModule: Module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(grantModule)
        modules(platformModule, grantPlatformModule)
        modules(
            infraModule,
            dataModule,
            domainModule,
            viewModelModule,
        )
    }
}

