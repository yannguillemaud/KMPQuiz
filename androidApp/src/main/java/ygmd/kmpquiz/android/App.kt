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
import ygmd.kmpquiz.data.database.AndroidDatabaseDriverFactory
import ygmd.kmpquiz.data.database.DatabaseProvider
import kotlin.time.ExperimentalTime

@ExperimentalTime
class App: Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()
        val driverFactory = AndroidDatabaseDriverFactory(applicationContext)
        DatabaseProvider.initialize(driverFactory)
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