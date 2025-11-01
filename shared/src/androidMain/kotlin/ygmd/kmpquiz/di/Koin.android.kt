package ygmd.kmpquiz.di

import android.content.Context
import androidx.work.WorkManager
import appVersionProvider.AndroidAppVersionProvider
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.Module
import org.koin.dsl.module
import scheduler.AndroidQuizWorkManager
import scheduler.AndroidSchedulerDataStore
import scheduler.platformScheduledCronsDataStore
import worker.QuizReminderWorker
import ygmd.kmpquiz.data.database.createDatabase
import ygmd.kmpquiz.data.database.sqlDriverFactory
import ygmd.kmpquiz.database.KMPQuizDatabase
import ygmd.kmpquiz.domain.appVersionProvider.AppVersionProvider
import ygmd.kmpquiz.domain.repository.SchedulerDataStore
import ygmd.kmpquiz.domain.scheduler.QuizWorkManager
import ygmd.kmpquiz.domain.scheduler.TaskScheduler
import ygmd.kmpquiz.infra.scheduler.CommonTaskScheduler

actual val platformModule: Module
    get() = module {
        worker {
            QuizReminderWorker(
                appContext = get(),
                workerParams = get()
            )
        }

        single<TaskScheduler> {
            CommonTaskScheduler(
                quizWorkManager = get(),
                schedulerDataStore = get(),
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

        single<QuizWorkManager> {
            AndroidQuizWorkManager(
                workManager = get()
            )
        }

        single<WorkManager> {
            WorkManager.getInstance(get())
        }

        single<SchedulerDataStore>{
            AndroidSchedulerDataStore(
                dataStore = get()
            )
        }

        single { get<Context>().platformScheduledCronsDataStore }

        single<AppVersionProvider>{
            AndroidAppVersionProvider(
                context = get()
            )
        }
    }