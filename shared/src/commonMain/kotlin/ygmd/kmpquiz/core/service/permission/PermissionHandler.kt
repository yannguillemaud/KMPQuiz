package ygmd.kmpquiz.core.service.permission

import ygmd.kmpquiz.core.domain.permission.PermissionOutcome

/**
 * Port abstracting a single-permission request flow (e.g. NOTIFICATION) away from any
 * concrete permissions library. Presentation-layer ViewModels depend only on this
 * interface — never on a library type such as Grant's `GrantHandler` — so `commonMain`
 * stays compilable on platforms without a permissions library (e.g. desktop).
 *
 * Implementations are stateful and scope-bound (see [PermissionHandlerFactory]); a new
 * instance is created per ViewModel via `viewModelScope`.
 */
interface PermissionHandler {

    /**
     * Refreshes the cached permission status against the OS (e.g. after the user returns
     * from system Settings). Fire-and-forget — no return value, mirrors Grant's
     * `GrantHandler.refreshStatus()`.
     */
    fun refreshStatus()

    /**
     * Requests the permission and suspends until the flow completes (granted, denied, or
     * dismissed), returning the final [PermissionOutcome].
     *
     * @param rationaleMessage optional message explaining why the permission is needed.
     * @param settingsMessage optional message shown when the user must open system Settings.
     */
    suspend fun requestSuspend(
        rationaleMessage: String? = null,
        settingsMessage: String? = null,
    ): PermissionOutcome

    /**
     * Requests the permission and invokes [onGranted] only when the request resolves to a
     * proceed-able outcome ([PermissionOutcome.Granted] or [PermissionOutcome.NotApplicable]).
     * Non-suspending callback form, mirrors Grant's `GrantHandler.request(...)`.
     */
    fun request(
        rationaleMessage: String? = null,
        settingsMessage: String? = null,
        onGranted: () -> Unit,
    )
}
