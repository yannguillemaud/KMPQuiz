package ygmd.kmpquiz.domain.usecase.notification

import dev.brewkits.grant.GrantAndServiceChecker
import dev.brewkits.grant.GrantManager

class SetUpSchedulingPermissionsUseCase(
    private val checker: GrantAndServiceChecker,
    private val grantManager: GrantManager,
) {
//    suspend operator fun invoke(permissionManager: PermissionManager): Result<Unit> {
//
//    }
}