package ygmd.kmpquiz.data.repository.permission

import ygmd.kmpquiz.core.repository.PermissionRepository

/**
 * Desktop [PermissionRepository] — there is no Grant-backed implementation on desktop
 * (see [ygmd.kmpquiz.core.service.permission.NoopPermissionHandler]). Both checks report
 * `true` so [ygmd.kmpquiz.core.usecase.scheduler.ToggleQuizSchedulerUseCase] proceeds,
 * matching the "no OS permission gate on desktop" behavior of the Noop permission handler.
 */
class NoopPermissionRepository : PermissionRepository {
    override suspend fun hasNotificationPermission(): Boolean = true
    override suspend fun hasExactAlarmPermission(): Boolean = true
}
