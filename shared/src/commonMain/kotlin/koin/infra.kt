package koin

import network.httpClientProvider
import org.koin.dsl.module
import ygmd.kmpquiz.data.service.QandaFetcher
import ygmd.kmpquiz.infra.openTrivia.OpenTriviaFetcher

val infraModule = module {
    factory { httpClientProvider() }

    factory<QandaFetcher> {
        OpenTriviaFetcher(
            client = get(),
        )
    }
}