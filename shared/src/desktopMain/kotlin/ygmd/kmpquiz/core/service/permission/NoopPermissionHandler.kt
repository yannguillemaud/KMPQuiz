package ygmd.kmpquiz.core.service.permission

import kotlinx.coroutines.CoroutineScope
import ygmd.kmpquiz.core.domain.permission.PermissionOutcome

/**
 * Desktop [PermissionHandler] — the Grant library has no desktop artifact, and
 * OS-level runtime permissions (NOTIFICATION, exact alarms) are not gated on desktop the
 * way they are on Android. Always reports [PermissionOutcome.NotApplicable] and proceeds
 * immediately, so gated actions (e.g. enabling a quiz scheduler) behave as if the
 * permission were already satisfied.
 */
class NoopPermissionHandler : PermissionHandler {

    override fun refreshStatus() {
        // No-op: there is no OS permission state to refresh on desktop.
    }

    override suspend fun requestSuspend(
        rationaleMessage: String?,
        settingsMessage: String?,
    ): PermissionOutcome = PermissionOutcome.NotApplicable

    override fun request(
        rationaleMessage: String?,
        settingsMessage: String?,
        onGranted: () -> Unit,
    ) {
        // Desktop has nothing to gate — proceed immediately, no return-value branch.
        onGranted()
    }
}

/** Desktop [PermissionHandlerFactory] — every scope gets the same stateless no-op handler. */
class NoopPermissionHandlerFactory : PermissionHandlerFactory {
    override fun create(scope: CoroutineScope): PermissionHandler = NoopPermissionHandler()
}
