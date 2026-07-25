package ygmd.kmpquiz.data.repository.permission

import dev.brewkits.grant.AppGrant
import dev.brewkits.grant.GrantManager
import dev.brewkits.grant.GrantStatus
import ygmd.kmpquiz.core.repository.PermissionRepository

class GrantPermissionRepository(
    private val grantManager: GrantManager
) : PermissionRepository {
    override suspend fun hasNotificationPermission(): Boolean {
        return grantManager.checkStatus(AppGrant.NOTIFICATION) == GrantStatus.GRANTED
    }

    override suspend fun hasExactAlarmPermission(): Boolean {
        return grantManager.checkStatus(AppGrant.SCHEDULE_EXACT_ALARM) == GrantStatus.GRANTED
    }
}
