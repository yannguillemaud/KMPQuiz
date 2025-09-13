package ygmd.kmpquiz.domain.di

import org.koin.dsl.module
import ygmd.kmpquiz.domain.usecase.fetch.DeleteFetchQandasUseCase
import ygmd.kmpquiz.domain.usecase.fetch.FetchQandasUseCase
import ygmd.kmpquiz.domain.usecase.fetch.GetFetchQandasUseCase
import ygmd.kmpquiz.domain.usecase.notification.RescheduleTasksUseCase
import ygmd.kmpquiz.domain.usecase.qanda.DeleteQandasUseCase
import ygmd.kmpquiz.domain.usecase.qanda.GetQandaUseCase
import ygmd.kmpquiz.domain.usecase.qanda.SaveQandasUseCase
import ygmd.kmpquiz.domain.usecase.quiz.CreateQuizUseCase
import ygmd.kmpquiz.domain.usecase.quiz.DeleteQuizUseCase
import ygmd.kmpquiz.domain.usecase.quiz.GetQuizUseCase
import ygmd.kmpquiz.domain.usecase.quiz.UpdateQuizUseCase
import ygmd.kmpquiz.domain.viewModel.coordinator.FetchScreenCoordinator

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
        DeleteQuizUseCase(
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
