package ygmd.kmpquiz.domain.di

import dev.brewkits.grant.impl.DefaultGrantManager
import org.koin.dsl.module
import ygmd.kmpquiz.domain.usecase.category.CategoryUseCase
import ygmd.kmpquiz.domain.usecase.cron.ToggleQuizSchedulerUseCase
import ygmd.kmpquiz.domain.usecase.fetch.FetchUseCase
import ygmd.kmpquiz.domain.usecase.notification.RescheduleTaskUseCase
import ygmd.kmpquiz.domain.usecase.notification.ScheduleQuizUseCase
import ygmd.kmpquiz.domain.usecase.qanda.DeleteQandasUseCase
import ygmd.kmpquiz.domain.usecase.qanda.GetQandaUseCase
import ygmd.kmpquiz.domain.usecase.qanda.GetQandaWithCategoryUseCase
import ygmd.kmpquiz.domain.usecase.qanda.SaveQandaUseCase
import ygmd.kmpquiz.domain.usecase.quiz.DeleteQuizUseCase
import ygmd.kmpquiz.domain.usecase.quiz.GetQuizUseCase
import ygmd.kmpquiz.domain.usecase.quiz.SaveQuizUseCase
import ygmd.kmpquiz.domain.usecase.quizSession.SessionResultsUseCase
import ygmd.kmpquiz.domain.usecase.quizSession.SetUpQuizSessionUseCase
import ygmd.kmpquiz.domain.viewModel.quiz.session.NextStateSessionUseCase
import ygmd.kmpquiz.domain.viewModel.quiz.session.SubmitAnswerUseCase

// Domain Layer - Use Cases
val domainModule = module {
    factory {
        FetchUseCase(
            fetchRepository = get(),
        )
    }

    factory {
        GetQandaUseCase(
            qandaRepository = get(),
            quizRepository = get()
        )
    }

    factory {
        SaveQandaUseCase(
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
        SetUpQuizSessionUseCase(
            repository = get(),
            getQandaUseCase = get(),
            quizRepository = get(),
        )
    }

    factory {
        SubmitAnswerUseCase(
            sessionRepository = get(),
        )
    }

    factory {
        NextStateSessionUseCase(
            sessionRepository = get(),
        )
    }

    factory {
        GetQandaWithCategoryUseCase(
            getQandaUseCase = get(),
            categoryUseCase = get(),
        )
    }

    factory {
        DeleteQuizUseCase(
            quizRepository = get(),
            quizScheduler = get(),
        )
    }

    factory {
        ToggleQuizSchedulerUseCase(
            quizRepository = get(),
            scheduleQuizUseCase = get(),
            permissionRepository = get()
        )
    }

    single {
        DefaultGrantManager(platformDelegate = get())
    }

    factory {
        ScheduleQuizUseCase(
            schedulerStore = get(),
            alarmScheduler = get()
        )
    }

    factory {
        SaveQuizUseCase(
            quizRepository = get(),
            schedulerStore = get(),
            alarmScheduler = get(),
        )
    }

    factory {
        CategoryUseCase(
            categoryRepository = get()
        )
    }

    factory {
        RescheduleTaskUseCase(
            getQuizUseCase = get(),
            scheduleQuizUseCase = get(),
        )
    }

    factory {
        SessionResultsUseCase(
            qandaUseCase = get(),
        )
    }
}
