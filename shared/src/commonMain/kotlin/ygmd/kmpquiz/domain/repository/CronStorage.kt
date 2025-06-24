package ygmd.kmpquiz.domain.repository

import ygmd.kmpquiz.domain.entities.cron.Cron

interface CronStorage {
    suspend fun loadAll(): List<Cron>
    suspend fun save(crons: List<Cron>): Result<Unit>
    suspend fun clear(): Result<Unit>
}