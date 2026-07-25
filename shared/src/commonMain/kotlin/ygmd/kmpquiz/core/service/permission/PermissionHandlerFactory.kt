package ygmd.kmpquiz.core.service.permission

import kotlinx.coroutines.CoroutineScope

/**
 * Creates a [PermissionHandler] bound to a caller-supplied [CoroutineScope].
 *
 * `PermissionHandler` implementations (and Grant's `GrantHandler` they may wrap on Android)
 * MUST be scoped to `viewModelScope` — a scope that only exists once a ViewModel is
 * constructed. Koin builds the dependency graph before that, so `PermissionHandler` cannot
 * be injected directly via a `factory { }` binding. Instead, ViewModels inject this factory
 * and call [create] with their own `viewModelScope` inside `init { }`.
 */
interface PermissionHandlerFactory {
    fun create(scope: CoroutineScope): PermissionHandler
}
