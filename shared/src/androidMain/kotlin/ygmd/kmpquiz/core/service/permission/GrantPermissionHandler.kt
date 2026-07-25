package ygmd.kmpquiz.core.service.permission

import dev.brewkits.grant.GrantHandler
import dev.brewkits.grant.GrantManager
import dev.brewkits.grant.GrantPermission
import dev.brewkits.grant.GrantStatus
import kotlinx.coroutines.CoroutineScope
import ygmd.kmpquiz.core.domain.permission.PermissionOutcome

/**
 * Android [PermissionHandler] adapter wrapping a Grant [GrantHandler].
 *
 * Maps [GrantStatus] to [PermissionOutcome]. **Only [GrantStatus.GRANTED] maps to
 * [PermissionOutcome.Granted]** — every other status maps to a blocking outcome
 * ([PermissionOutcome.Denied] / [PermissionOutcome.DeniedAlways]).
 * [PermissionOutcome.NotApplicable] is intentionally never emitted here: on Android the
 * permission concept always applies, so callers that treat `Granted OR NotApplicable` as
 * "proceed" (see [ygmd.kmpquiz.presentation.viewModel.quiz.QuizViewModel.toggleScheduler])
 * keep today's exact `== GRANTED` behavior.
 *
 * [grantHandler] is exposed so the Android `actual PermissionRationaleDialog` can reach
 * the concrete [GrantHandler] required by Grant's `GrantDialog` composable (which needs
 * the concrete class, not a port interface, to observe `GrantUiState`).
 */
class GrantPermissionHandler(
    val grantHandler: GrantHandler,
) : PermissionHandler {

    override fun refreshStatus() {
        grantHandler.refreshStatus()
    }

    override suspend fun requestSuspend(
        rationaleMessage: String?,
        settingsMessage: String?,
    ): PermissionOutcome {
        val status = grantHandler.requestSuspend(rationaleMessage, settingsMessage)
        return status.toPermissionOutcome()
    }

    override fun request(
        rationaleMessage: String?,
        settingsMessage: String?,
        onGranted: () -> Unit,
    ) {
        // Forward only — do NOT re-map the callback's GrantStatus. GrantHandler.request's
        // onGranted callback already fires exclusively on GRANTED/PARTIAL_GRANTED per its
        // own state machine; wrapping it bridges the (GrantStatus) -> Unit signature to the
        // port's () -> Unit without altering when it fires.
        grantHandler.request(rationaleMessage, settingsMessage) { _ -> onGranted() }
    }

    private fun GrantStatus.toPermissionOutcome(): PermissionOutcome = when (this) {
        GrantStatus.GRANTED -> PermissionOutcome.Granted
        GrantStatus.DENIED_ALWAYS -> PermissionOutcome.DeniedAlways
        GrantStatus.PARTIAL_GRANTED,
        GrantStatus.DENIED,
        GrantStatus.NOT_DETERMINED,
        GrantStatus.BUSY -> PermissionOutcome.Denied
    }
}

/**
 * Android [PermissionHandlerFactory] — creates a [GrantPermissionHandler] wrapping a fresh
 * [GrantHandler] bound to the caller-supplied scope (must be `viewModelScope`, per
 * [GrantHandler]'s own scope contract).
 */
class GrantPermissionHandlerFactory(
    private val grantManager: GrantManager,
    private val grant: GrantPermission,
) : PermissionHandlerFactory {
    override fun create(scope: CoroutineScope): PermissionHandler =
        GrantPermissionHandler(GrantHandler(grantManager, grant, scope))
}
