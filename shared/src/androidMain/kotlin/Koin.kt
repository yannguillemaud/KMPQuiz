
import androidx.work.WorkManager
import org.koin.dsl.module
import scheduler.AndroidTaskScheduler
import ygmd.kmpquiz.application.scheduler.TaskScheduler

val androidMainModule = module {
    single { WorkManager.getInstance(get()) }
    single<TaskScheduler> {
        AndroidTaskScheduler(
            workManager = get(),
            cronExecutionCalculator = get()
        )
    }
}