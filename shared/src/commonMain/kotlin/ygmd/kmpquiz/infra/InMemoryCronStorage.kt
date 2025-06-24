package ygmd.kmpquiz.infra

import ygmd.kmpquiz.domain.entities.cron.Cron
import ygmd.kmpquiz.domain.repository.CronStorage

class InMemoryCronStorage: CronStorage {
    private var _crons = mutableListOf<Cron>()

    override suspend fun loadAll(): List<Cron> {
        return _crons
    }

    override suspend fun save(crons: List<Cron>): Result<Unit> {
        _crons = crons.toMutableList()
        return Result.success(Unit)
    }

    override suspend fun clear(): Result<Unit> {
        _crons = mutableListOf()
        return Result.success(Unit)
    }
}