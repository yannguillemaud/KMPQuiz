package ygmd.kmpquiz.android

import android.app.Application
import androidMainModule
import koin.dataModule
import koin.domainModule
import koin.infraModule
import koin.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import kotlin.time.ExperimentalTime

@ExperimentalTime
class App: Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                infraModule,
                dataModule,
                domainModule,
                viewModelModule,
                androidMainModule,
            )
        }
    }
}