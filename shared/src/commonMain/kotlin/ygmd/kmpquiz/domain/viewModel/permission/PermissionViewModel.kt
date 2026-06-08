package ygmd.kmpquiz.domain.viewModel.permission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.brewkits.grant.AppGrant
import dev.brewkits.grant.GrantHandler
import dev.brewkits.grant.GrantManager
import dev.brewkits.grant.GrantStatus
import kotlinx.coroutines.launch

class PermissionViewModel(
    private val grantManager: GrantManager
): ViewModel() {
    val scope = viewModelScope
    val notificationHandler = GrantHandler(
        grantManager = grantManager,
        grant = AppGrant.NOTIFICATION,
        scope = scope
    )

    val hasNotificationPermission = notificationHandler.requestFlow()

    fun requestNotificationPermission(callback: (GrantStatus) -> Unit) {
        scope.launch {
            notificationHandler.request { callback(it) }
        }
    }
}