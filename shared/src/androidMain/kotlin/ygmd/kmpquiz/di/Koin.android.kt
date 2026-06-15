package ygmd.kmpquiz.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.work.WorkManager
import app.cash.sqldelight.db.SqlDriver
import dev.brewkits.grant.AndroidSavedStateDelegate
import dev.brewkits.grant.InMemoryGrantStore
import dev.brewkits.grant.SavedStateDelegate
import dev.brewkits.grant.di.grantPlatformModule
import dev.brewkits.grant.impl.PlatformGrantDelegate
import infra.notifier.AndroidNotificationSender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module
import scheduler.AndroidAlarmScheduler
import ygmd.kmpquiz.data.database.createDatabase
import ygmd.kmpquiz.data.database.sqlDriverFactory
import ygmd.kmpquiz.database.KMPQuizDatabase
import ygmd.kmpquiz.core.service.notification.NotificationSender
import ygmd.kmpquiz.core.service.scheduler.QuizAlarmScheduler

actual val platformModule: Module
    get() = module {
        single<NotificationSender> { AndroidNotificationSender(context = get()) }
        single<QuizAlarmScheduler> { AndroidAlarmScheduler(context = get()) }
        single<SqlDriver> { sqlDriverFactory() }
        single<KMPQuizDatabase> { createDatabase(driver = get()) }
        single<WorkManager> { WorkManager.getInstance(get()) }
        single<CoroutineScope> { CoroutineScope(Dispatchers.IO + SupervisorJob()) }
        single<DataStore<Preferences>> {
            PreferenceDataStoreFactory.createWithPath(
                scope = get<CoroutineScope>(),
                produceFile = {
                    get<Context>().filesDir
                        .resolve("quiz_schedulers_map.preferences_pb")
                        .absolutePath
                        .toPath()
                }
            )
        }
        single {
            PlatformGrantDelegate(context = get(), store = InMemoryGrantStore())
        }
        single<SavedStateDelegate> { AndroidSavedStateDelegate(savedStateHandle = get()) }
    }