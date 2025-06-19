package koin

import org.koin.dsl.module
import ygmd.kmpquiz.domain.usecase.DeleteQandasUseCase
import ygmd.kmpquiz.domain.usecase.DeleteQandasUseCaseImpl
import ygmd.kmpquiz.domain.usecase.FetchQandasUseCase
import ygmd.kmpquiz.domain.usecase.GetQandasUseCase
import ygmd.kmpquiz.domain.usecase.GetQandasUseCaseImpl
import ygmd.kmpquiz.domain.usecase.QuizUseCase
import ygmd.kmpquiz.domain.usecase.QuizUseCaseImpl
import ygmd.kmpquiz.domain.usecase.SaveQandasUseCase
import ygmd.kmpquiz.domain.usecase.SaveQandasUseCaseImpl

// Domain Layer - Use Cases
val domainModule = module {
    // Qanda Use Cases
    factory<GetQandasUseCase> {
        GetQandasUseCaseImpl(
            repository = get()
        )
    }

    factory<SaveQandasUseCase> {
        SaveQandasUseCaseImpl(
            repository = get(),
            logger = get()
        )
    }

    factory<DeleteQandasUseCase> {
        DeleteQandasUseCaseImpl(
            repository = get(),
            logger = get()
        )
    }

    // Quiz Use Cases
    factory<QuizUseCase> {
        QuizUseCaseImpl(
            repository = get(),
            logger = get()
        )
    }

    // Fetch Use Cases
    factory {
        FetchQandasUseCase(
            fetcher = get()
        )
    }
}
