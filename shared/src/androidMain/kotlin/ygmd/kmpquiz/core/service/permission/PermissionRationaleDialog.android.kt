package ygmd.kmpquiz.core.service.permission

import androidx.compose.runtime.Composable
import dev.brewkits.grant.compose.GrantDialog

/**
 * Android actual. Grant's `GrantDialog` composable requires the concrete `GrantHandler`
 * (it observes `GrantHandler.state` directly) rather than the [PermissionHandler] port, so
 * this downcasts to [GrantPermissionHandler] and unwraps its `grantHandler`.
 * [GrantPermissionHandler] is the only Android [PermissionHandler] implementation in this
 * app (built exclusively by `GrantPermissionHandlerFactory`), so the downcast is safe by
 * construction; the null-safe cast simply no-ops instead of crashing if that ever changes.
 */
@Composable
actual fun PermissionRationaleDialog(handler: PermissionHandler) {
    val grantPermissionHandler = handler as? GrantPermissionHandler ?: return
    GrantDialog(handler = grantPermissionHandler.grantHandler)
}
