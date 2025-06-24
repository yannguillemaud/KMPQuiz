package ygmd.kmpquiz.application.usecase.cron

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.entities.cron.Cron
import ygmd.kmpquiz.domain.repository.CronRepository

class GetCronUseCase(
    private val cronRepository: CronRepository,
) {
    fun observeAll(): Flow<List<Cron>> = cronRepository.observeAll()
    suspend fun getAllCrons(): List<Cron> = cronRepository.getAll()
    suspend fun getCronForQanda(qandaId: Long): Cron? = cronRepository.getByQandaId(qandaId)
    suspend fun getCronForCategory(category: String): List<Cron> =
        cronRepository.getByCategory(category)
    suspend fun getGlobalCron(): Cron? = cronRepository.getGlobal()
}