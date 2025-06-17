package ygmd.kmpquiz.data.service

import ygmd.kmpquiz.data.repository.service.FetchResult
import ygmd.kmpquiz.domain.entities.qanda.Qanda

interface QandaFetcher {
    val enabled: Boolean
    suspend fun fetch(fetchConfig: FetchConfig): FetchResult<List<Qanda>>
}

sealed interface QandaSource {
    val name: String
    val description: String?

    data object OPEN_TRIVIA: QandaSource {
        override val name: String = "OpenTrivia"
        override val description: String
            get() = TODO("Not yet implemented")
    }

    data object COUNTER_STRIKE: QandaSource {
        override val name: String = "The Trivia API"
        override val description: String
            get() = TODO("Not yet implemented")
    }
}

data class FetchConfig(
    val amount: Int? = 20,
    val category: String? = null,
    val difficulty: String? = null,
)