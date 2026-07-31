package ygmd.kmpquiz.di

import org.koin.core.module.Module
import org.koin.dsl.module
import ygmd.kmpquiz.core.repository.PermissionRepository
import ygmd.kmpquiz.core.scheduler.NoopQuizAlarmScheduler
import ygmd.kmpquiz.core.service.permission.NoopPermissionHandlerFactory
import ygmd.kmpquiz.core.service.permission.PermissionHandlerFactory
import ygmd.kmpquiz.core.service.scheduler.QuizAlarmScheduler
import ygmd.kmpquiz.data.repository.permission.NoopPermissionRepository

/**
 * Desktop `platformModule` actual. Grant has no desktop artifact, so unlike the Android
 * actual this module never includes a Grant Koin module — only the permission port's
 * no-op bindings (B6/C4). `SqlDriver`/`KMPQuizDatabase` need no binding here: they're
 * resolved once in the common `infraModule` via `sqlDriverFactory()`, whose desktop
 * `actual` (JdbcSqliteDriver under `%APPDATA%\KMPQuiz`) now exists —
 * `data/database/DatabaseDriverFactory.desktop.kt`. `NotificationSender`/
 * `QuizAlarmScheduler`/etc. are still missing and tracked outside this refactor; see
 * docs/refactorization/2026-07-24-desktop-grant-koin-backhandler.md.
 */
actual val platformModule: Module
    get() = module {
        single<PermissionRepository> { NoopPermissionRepository() }
        single<PermissionHandlerFactory> { NoopPermissionHandlerFactory() }
        single<QuizAlarmScheduler> { NoopQuizAlarmScheduler() }
    }
