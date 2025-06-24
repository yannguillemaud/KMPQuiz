package ygmd.kmpquiz.domain.service

import ygmd.kmpquiz.domain.repository.CronRepository
import ygmd.kmpquiz.domain.entities.cron.CronExpression

class CronResolutionStrategy(
    private val cronRepository: CronRepository
) {
    suspend fun getEffectiveCron(qandaId: Long, category: String): CronExpression? {
        cronRepository.getByQandaId(qandaId)?.let { return it.expression }
        cronRepository.getByCategory(category).firstOrNull()?.let { return it.expression }
        return cronRepository.getGlobal()?.expression
    }
}