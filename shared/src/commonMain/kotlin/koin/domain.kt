package koin

import org.koin.dsl.module
import ygmd.kmpquiz.application.usecase.cron.GetCronUseCase
import ygmd.kmpquiz.application.usecase.notification.GetNotificationUseCase
import ygmd.kmpquiz.application.usecase.qanda.DeleteQandasUseCase
import ygmd.kmpquiz.application.usecase.qanda.DeleteQandasUseCaseImpl
import ygmd.kmpquiz.application.usecase.qanda.FetchQandasUseCase
import ygmd.kmpquiz.application.usecase.qanda.GetQandasUseCase
import ygmd.kmpquiz.application.usecase.qanda.GetQandasUseCaseImpl
import ygmd.kmpquiz.application.usecase.qanda.SaveQandasUseCase
import ygmd.kmpquiz.application.usecase.qanda.SaveQandasUseCaseImpl
import ygmd.kmpquiz.application.usecase.quiz.QuizUseCase
import ygmd.kmpquiz.application.usecase.quiz.QuizUseCaseImpl

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
        )
    }

    factory<DeleteQandasUseCase> {
        DeleteQandasUseCaseImpl(
            repository = get(),
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

    factory {
        GetCronUseCase(
            cronRepository = get()
        )
    }

    factory {
        GetNotificationUseCase(
//            notificationRepository = get()
        )
    }
}
