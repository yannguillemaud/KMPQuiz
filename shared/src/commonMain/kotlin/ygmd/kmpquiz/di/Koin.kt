package ygmd.kmpquiz.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import ygmd.kmpquiz.data.di.dataModule
import ygmd.kmpquiz.core.di.domainModule
import ygmd.kmpquiz.presentation.viewModel.viewModelModule
import ygmd.kmpquiz.infra.di.infraModule

// Grant (dev.brewkits.grant) has no desktop artifact, so grantModule/grantPlatformModule
// are no longer installed here directly — commonMain must stay Grant-free for the desktop
// target to compile. The Android `platformModule` actual `includes(grantModule,
// grantPlatformModule)` itself; desktop's actual has no Grant dependency at all. See B4 in
// docs/refactorization/2026-07-24-desktop-grant-koin-backhandler.md.
expect val platformModule: Module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(platformModule)
        modules(
            infraModule,
            dataModule,
            domainModule,
            viewModelModule,
        )
    }
}

