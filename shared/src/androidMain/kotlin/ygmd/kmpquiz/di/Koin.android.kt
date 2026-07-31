package ygmd.kmpquiz.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.work.WorkManager
import dev.brewkits.grant.AndroidSavedStateDelegate
import dev.brewkits.grant.AppGrant.NOTIFICATION
import dev.brewkits.grant.GrantManager
import dev.brewkits.grant.InMemoryGrantStore
import dev.brewkits.grant.SavedStateDelegate
import dev.brewkits.grant.di.grantModule
import dev.brewkits.grant.di.grantPlatformModule
import dev.brewkits.grant.impl.DefaultGrantManager
import dev.brewkits.grant.impl.PlatformGrantDelegate
import infra.notifier.AndroidNotificationSender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module
import scheduler.AndroidAlarmScheduler
import ygmd.kmpquiz.core.repository.PermissionRepository
import ygmd.kmpquiz.core.service.permission.GrantPermissionHandlerFactory
import ygmd.kmpquiz.core.service.permission.PermissionHandlerFactory
import ygmd.kmpquiz.data.repository.permission.GrantPermissionRepository
import ygmd.kmpquiz.core.service.notification.NotificationSender
import ygmd.kmpquiz.core.service.scheduler.QuizAlarmScheduler

actual val platformModule: Module
    get() = module {
        // Grant (permissions) is Android/iOS-only — installed here, not at the top-level
        // initKoin(), so commonMain/desktop stay Grant-free. See B4 in
        // docs/refactorization/2026-07-24-desktop-grant-koin-backhandler.md.
        includes(grantModule, grantPlatformModule)

        single<NotificationSender> { AndroidNotificationSender(context = get()) }
        single<QuizAlarmScheduler> { AndroidAlarmScheduler(context = get()) }
        // SqlDriver/KMPQuizDatabase are bound once in the common infraModule via the
        // sqlDriverFactory() expect/actual — do not rebind here (was a silently-tolerated
        // Koin double binding; consolidated ahead of adding the desktop actual, see
        // docs/features/desktop-target.md §"Double binding").
        single<WorkManager> { WorkManager.getInstance(get()) }
        single<CoroutineScope> { CoroutineScope(Dispatchers.IO + SupervisorJob()) }
        single {
            PlatformGrantDelegate(context = get(), store = InMemoryGrantStore())
        }
        single<SavedStateDelegate> { AndroidSavedStateDelegate(savedStateHandle = get()) }

        // Relocated from core/di/domain.kt (B3) — kept as a `single`, matching Grant's own
        // scope contract for GrantManager. Redundant alongside grantModule's own
        // `single<GrantManager>` binding (different Koin key: concrete type here vs.
        // interface there), but nothing consumes the concrete type — moved verbatim per plan.
        single {
            DefaultGrantManager(platformDelegate = get())
        }

        // Relocated from data/di/data.kt (B2).
        single<PermissionRepository> {
            GrantPermissionRepository(grantManager = get())
        }

        // C7: PermissionHandlerFactory is the seam ViewModels use to build a
        // viewModelScope-bound PermissionHandler at construction time.
        single<PermissionHandlerFactory> {
            GrantPermissionHandlerFactory(grantManager = get<GrantManager>(), grant = NOTIFICATION)
        }
    }