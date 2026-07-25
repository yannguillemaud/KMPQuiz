package ygmd.kmpquiz.core.service.permission

import androidx.compose.runtime.Composable
import ygmd.kmpquiz.core.service.permission.PermissionHandler

/**
 * Renders the platform-specific rationale/settings-guide dialog for a [PermissionHandler]'s
 * in-flight request, if any. On Android this wraps Grant's `GrantDialog`; on desktop it is a
 * no-op since [ygmd.kmpquiz.core.service.permission.NoopPermissionHandler] never enters a
 * blocking state. Call sites (`QuizzesScreen`, `QuizEditorScreen`) depend only on this
 * `expect` function, never on a concrete permissions library.
 */
@Composable
expect fun PermissionRationaleDialog(handler: PermissionHandler)
