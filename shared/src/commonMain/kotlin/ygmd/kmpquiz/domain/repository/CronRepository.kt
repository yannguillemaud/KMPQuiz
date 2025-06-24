package ygmd.kmpquiz.domain.repository

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.entities.cron.Cron

interface CronRepository {
    /* OBSERVABILITY */
    fun observeAll(): Flow<List<Cron>>

    /* LECTURE */
    suspend fun getAll(): List<Cron>
    suspend fun getById(id: String): Cron?
    suspend fun getByQandaId(id: Long): Cron?
    suspend fun getByCategory(category: String): List<Cron>
    suspend fun getGlobal(): Cron?

    /* ECRITURE */
    suspend fun save(cron: Cron): Result<Unit>
    suspend fun delete(id: String): Result<Unit>
    suspend fun deleteByCategory(category: String): Result<Unit>
}