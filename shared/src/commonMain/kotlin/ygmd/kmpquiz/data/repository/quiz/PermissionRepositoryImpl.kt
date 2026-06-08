package ygmd.kmpquiz.data.repository.quiz

import dev.brewkits.grant.AppGrant
import dev.brewkits.grant.GrantManager
import ygmd.kmpquiz.domain.repository.PermissionRepository

class PermissionRepositoryImpl(
    private val grantManager: GrantManager
) : PermissionRepository {
    override suspend fun hasNotificationPermission(): Boolean {
        return grantManager.checkStatus(AppGrant.NOTIFICATION) == dev.brewkits.grant.GrantStatus.GRANTED
    }

    override suspend fun hasExactAlarmPermission(): Boolean {
        return grantManager.checkStatus(AppGrant.SCHEDULE_EXACT_ALARM) == dev.brewkits.grant.GrantStatus.GRANTED
    }
}