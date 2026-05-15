package ygmd.kmpquiz.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import ygmd.kmpquiz.data.di.dataModule
import ygmd.kmpquiz.domain.di.domainModule
import ygmd.kmpquiz.domain.viewModel.viewModelModule
import ygmd.kmpquiz.infra.di.infraModule

expect val platformModule: Module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(
            modules = listOf(
                infraModule,
                dataModule,
                domainModule,
                viewModelModule,
            )
        )
    }
}

