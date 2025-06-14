package ygmd.kmpquiz.domain.service

import ygmd.kmpquiz.domain.pojo.qanda.QandaContent

interface QandaFetcher {
    val source: QandaSource
    suspend fun fetch(): FetchResult<List<QandaContent>>
}

interface FetchQanda {
    suspend fun fetch(source: QandaSource = QandaSource.DEFAULT): FetchResult<List<QandaContent>>
}

enum class QandaSource {
    OPEN_TRIVIA,
    COUNTER_STRIKE,
    DEFAULT
}