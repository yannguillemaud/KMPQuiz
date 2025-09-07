
import androidx.work.WorkManager
import org.koin.dsl.module
import scheduler.AndroidTaskScheduler
import ygmd.kmpquiz.application.scheduler.TaskScheduler
import ygmd.kmpquiz.data.database.AndroidDatabaseDriverFactory
import ygmd.kmpquiz.data.database.DatabaseDriverFactory
import ygmd.kmpquiz.data.database.createDatabase
import ygmd.kmpquiz.database.KMPQuizDatabase

val androidMainModule = module {
    single { WorkManager.getInstance(get()) }
    single<TaskScheduler> {
        AndroidTaskScheduler(
            workManager = get(),
            cronExecutionCalculator = get()
        )
    }
    single<DatabaseDriverFactory> {
        AndroidDatabaseDriverFactory(context = get())
    }

    single<KMPQuizDatabase> {
        createDatabase(
            driverFactory = get(),
            isDev = true
        )
    }
}