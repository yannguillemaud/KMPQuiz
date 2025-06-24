package koin

import network.httpClientProvider
import org.koin.dsl.module
import ygmd.kmpquiz.data.service.QandaFetcher
import ygmd.kmpquiz.domain.repository.CronStorage
import ygmd.kmpquiz.infra.InMemoryCronStorage
import ygmd.kmpquiz.infra.openTrivia.OpenTriviaFetcher

val infraModule = module {
    factory { httpClientProvider() }

    single<CronStorage> {
        InMemoryCronStorage()
    }

    factory<QandaFetcher> {
        OpenTriviaFetcher(
            client = get(),
        )
    }
}