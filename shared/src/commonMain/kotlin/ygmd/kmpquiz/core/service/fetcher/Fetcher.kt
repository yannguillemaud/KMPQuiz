package ygmd.kmpquiz.core.service.fetcher

import ygmd.kmpquiz.core.domain.qanda.QandaDetails

interface Fetcher {
    val name: String
    suspend operator fun invoke(): Result<List<QandaDetails>>
}