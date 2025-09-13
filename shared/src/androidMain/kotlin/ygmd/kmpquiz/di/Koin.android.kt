package ygmd.kmpquiz.di

import androidx.work.WorkManager
import org.koin.core.module.Module
import org.koin.dsl.module
import scheduler.AndroidTaskScheduler
import ygmd.kmpquiz.data.database.createDatabase
import ygmd.kmpquiz.data.database.sqlDriverFactory
import ygmd.kmpquiz.database.KMPQuizDatabase
import ygmd.kmpquiz.domain.scheduler.TaskScheduler

actual val platformModule: Module
    get() = module {
        single { WorkManager.getInstance(get()) }

        single<TaskScheduler> {
            AndroidTaskScheduler(
                workManager = get(),
                cronExecutionCalculator = get()
            )
        }

        single {
            sqlDriverFactory()
        }

        single<KMPQuizDatabase> {
            createDatabase(
                driver = get()
            )
        }
    }