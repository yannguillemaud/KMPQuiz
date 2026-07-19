package ygmd.kmpquiz.core.di

import dev.brewkits.grant.impl.DefaultGrantManager
import org.koin.dsl.module
import ygmd.kmpquiz.core.service.notification.NotificationHandler
import ygmd.kmpquiz.core.usecase.scheduler.ComputeQuizSchedulerNextTriggerUseCase
import ygmd.kmpquiz.core.usecase.category.CategoryUseCase
import ygmd.kmpquiz.core.usecase.fetcher.GetFetchersUseCase
import ygmd.kmpquiz.core.usecase.scheduler.ToggleQuizSchedulerUseCase
import ygmd.kmpquiz.core.usecase.qanda.DownloadQandasUseCase
import ygmd.kmpquiz.core.usecase.notification.OnReceiveQuizNotificationUseCase
import ygmd.kmpquiz.core.usecase.notification.ScheduleQuizUseCase
import ygmd.kmpquiz.core.usecase.qanda.DeleteQandasUseCase
import ygmd.kmpquiz.core.usecase.qanda.GetQandaUseCase
import ygmd.kmpquiz.core.usecase.qanda.GetQandaWithCategoryUseCase
import ygmd.kmpquiz.core.usecase.quiz.DeleteQuizUseCase
import ygmd.kmpquiz.core.usecase.quiz.GetQuizUseCase
import ygmd.kmpquiz.core.usecase.quiz.SaveQuizUseCase
import ygmd.kmpquiz.core.usecase.session.SessionQuestionReviewUseCase
import ygmd.kmpquiz.core.usecase.session.SessionResultsUseCase
import ygmd.kmpquiz.core.usecase.session.SetUpQuizSessionUseCase
import ygmd.kmpquiz.presentation.viewModel.quiz.session.NextStateSessionUseCase
import ygmd.kmpquiz.presentation.viewModel.quiz.session.SubmitAnswerUseCase

// Domain Layer - Use Cases
val domainModule = module {
    factory {
        GetQandaUseCase(
            qandaRepository = get(),
            quizRepository = get()
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
            quizAlarmScheduler = get(),
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
            alarmScheduler = get(),
            quizRepository = get(),
            computeQuizSchedulerNextTriggerUseCase = get(),
        )
    }

    factory {
        SaveQuizUseCase(
            quizRepository = get(),
            computeQuizSchedulerNextTriggerUseCase = get(),
            alarmScheduler = get(),
        )
    }

    factory {
        CategoryUseCase(
            quizRepository = get(),
            categoryRepository = get()
        )
    }

    factory {
        SessionResultsUseCase(
            qandaUseCase = get(),
        )
    }

    factory {
        SessionQuestionReviewUseCase(
            getQandaUseCase = get(),
            categoryUseCase = get(),
        )
    }

    factory<NotificationHandler> {
        OnReceiveQuizNotificationUseCase(
            quizRepository = get(),
            quizAlarmScheduler = get(),
            notificationSender = get(),
            computeQuizSchedulerNextTriggerUseCase = get()
        )
    }

    single {
        ComputeQuizSchedulerNextTriggerUseCase()
    }

    factory {
        DownloadQandasUseCase(
            fetcherRepository = get(),
            qandaRepository = get(),
            categoryRepository = get(),
        )
    }

    factory {
        GetQandaUseCase(
            qandaRepository = get(),
            quizRepository = get()
        )
    }

    factory {
        GetFetchersUseCase(
            fetcherRepository = get()
        )
    }
}
