package koin

import org.koin.dsl.module
import ygmd.kmpquiz.application.usecase.fetch.DeleteFetchQandasUseCase
import ygmd.kmpquiz.application.usecase.fetch.FetchQandasUseCase
import ygmd.kmpquiz.application.usecase.fetch.GetFetchQandasUseCase
import ygmd.kmpquiz.application.usecase.notification.RescheduleTasksUseCase
import ygmd.kmpquiz.application.usecase.qanda.DeleteQandasUseCase
import ygmd.kmpquiz.application.usecase.qanda.GetQandaUseCase
import ygmd.kmpquiz.application.usecase.qanda.SaveQandasUseCase
import ygmd.kmpquiz.application.usecase.quiz.CreateQuizUseCase
import ygmd.kmpquiz.application.usecase.quiz.GetQuizUseCase
import ygmd.kmpquiz.application.usecase.quiz.UpdateQuizUseCase
import ygmd.kmpquiz.viewModel.coordinator.FetchScreenCoordinator

// Domain Layer - Use Cases
val domainModule = module {
    // Qanda Use Cases
    factory {
        GetQandaUseCase(
            repository = get()
        )
    }

    factory {
        SaveQandasUseCase (
            repository = get(),
        )
    }

    factory {
        DeleteQandasUseCase(
            repository = get(),
        )
    }

    // Quiz Use Cases
    factory {
        GetQuizUseCase(quizRepository = get())
    }

    factory {
        CreateQuizUseCase(
            quizRepository = get(),
            getQandaUseCase = get(),
            getQuizUseCase = get(),
        )
    }

    // Fetch Use Cases
    factory {
        GetFetchQandasUseCase(
            repository = get()
        )
    }

    factory {
        FetchQandasUseCase(
            fetcher = get(),
            fetchRepository = get()
        )
    }

    factory {
        DeleteFetchQandasUseCase(
            fetchRepository = get()
        )
    }

    factory {
        UpdateQuizUseCase(
            quizRepository = get()
        )
    }

    factory {
        RescheduleTasksUseCase(
            taskScheduler = get(),
            quizRepository = get(),
        )
    }

    factory {
        FetchScreenCoordinator(
            fetchQandasViewModel = get(),
            savedQandasViewModel = get()
        )
    }
}
