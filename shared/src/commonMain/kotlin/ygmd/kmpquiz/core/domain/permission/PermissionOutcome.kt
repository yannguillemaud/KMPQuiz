package ygmd.kmpquiz.core.domain.permission

/**
 * Platform-agnostic result of a permission check/request, returned by [ygmd.kmpquiz.core.service.permission.PermissionHandler].
 *
 * This decouples presentation-layer consumers (ViewModels) from any concrete permissions
 * library (e.g. Grant on Android) — `core`/`presentation` only ever see this sealed type.
 */
sealed interface PermissionOutcome {

    /** The permission is granted; the gated action may proceed. */
    data object Granted : PermissionOutcome

    /**
     * The permission concept does not apply on this platform (e.g. desktop, where OS-level
     * runtime permissions like NOTIFICATION are not gated the same way as Android). Treated the
     * same as [Granted] by callers — the gated action proceeds.
     */
    data object NotApplicable : PermissionOutcome

    /** The permission was denied but may be requested again (rationale can still be shown). */
    data object Denied : PermissionOutcome

    /** The permission was permanently denied; the user must change it from system settings. */
    data object DeniedAlways : PermissionOutcome
}
