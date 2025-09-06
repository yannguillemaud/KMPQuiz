
import androidx.work.WorkManager
import org.koin.dsl.module
import scheduler.AndroidTaskScheduler
import ygmd.kmpquiz.application.scheduler.TaskScheduler
import ygmd.kmpquiz.data.database.DatabaseDriverFactory

val androidMainModule = module {
    single { WorkManager.getInstance(get()) }
    single<TaskScheduler> {
        AndroidTaskScheduler(
            workManager = get(),
            cronExecutionCalculator = get()
        )
    }
    single {
        DatabaseDriverFactory(get())
    }
}