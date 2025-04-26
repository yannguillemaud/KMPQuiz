package ygmd.kmpquiz.domain.di

import org.koin.dsl.module
import ygmd.kmpquiz.domain.usecase.category.CategoryUseCase
import ygmd.kmpquiz.domain.usecase.cron.ToggleCronUseCase
import ygmd.kmpquiz.domain.usecase.fetch.FetchUseCase
import ygmd.kmpquiz.domain.usecase.fetch.GetFetchersUseCase
import ygmd.kmpquiz.domain.usecase.notification.RescheduleTasksUseCase
import ygmd.kmpquiz.domain.usecase.qanda.DeleteQandasUseCase
import ygmd.kmpquiz.domain.usecase.qanda.GetQandaUseCase
import ygmd.kmpquiz.domain.usecase.qanda.QandaEditUseCase
import ygmd.kmpquiz.domain.usecase.qanda.SaveQandasUseCase
import ygmd.kmpquiz.domain.usecase.quiz.CreateQuizUseCase
import ygmd.kmpquiz.domain.usecase.quiz.DeleteQuizUseCase
import ygmd.kmpquiz.domain.usecase.quiz.GetQuizUseCase
import ygmd.kmpquiz.domain.usecase.quiz.QuizEditUseCase
import ygmd.kmpquiz.domain.usecase.quizSession.QuizSessionUseCase

// Domain Layer - Use Cases
val domainModule = module {
    factory {
        GetFetchersUseCase(
            fetchersRepository = get()
        )
    }

    factory {
        FetchUseCase(
            fetchRepository = get(),
        )
    }

    factory {
        GetQandaUseCase(
            repository = get()
        )
    }

    factory {
        SaveQandasUseCase(
            qandaRepository = get(),
            categoryRepository = get(),
        )
    }

    factory {
        DeleteQandasUseCase(
            repository = get(),
        )
    }

    factory {
        GetQuizUseCase(quizRepository = get())
    }

    factory {
        QuizSessionUseCase(quizSessionRepository = get())
    }

    factory {
        CreateQuizUseCase(
            quizRepository = get(),
        )
    }

    factory {
        DeleteQuizUseCase(
            quizRepository = get()
        )
    }

    factory {
        ToggleCronUseCase(
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
        QuizEditUseCase(
            quizRepository = get(),
            quizEditRepository = get(),
            qandaRepository = get(),
        )
    }

    factory {
        QandaEditUseCase(
            qandaRepository = get(),
            qandaEditRepository = get(),
            categoryRepository = get(),
        )
    }

    factory {
        CategoryUseCase(get())
    }
}
