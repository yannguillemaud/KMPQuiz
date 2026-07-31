package ygmd.kmpquiz.core.service.permission

import androidx.compose.runtime.Composable

/**
 * Desktop actual — no-op. [NoopPermissionHandler]
 * always proceeds immediately and never enters a rationale/settings-guide state, so there
 * is nothing to render.
 */
@Composable
actual fun PermissionRationaleDialog(handler: PermissionHandler) {
    // Intentionally empty.
}
