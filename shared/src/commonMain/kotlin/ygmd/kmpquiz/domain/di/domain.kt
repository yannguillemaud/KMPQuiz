package ygmd.kmpquiz.domain.di

import dev.brewkits.grant.impl.DefaultGrantManager
import org.koin.dsl.module
import ygmd.kmpquiz.domain.scheduler.TimeProvider
import ygmd.kmpquiz.domain.usecase.category.CategoryUseCase
import ygmd.kmpquiz.domain.usecase.cron.ToggleQuizSchedulerUseCase
import ygmd.kmpquiz.domain.usecase.fetch.FetchUseCase
import ygmd.kmpquiz.domain.usecase.notification.ScheduleQuizUseCase
import ygmd.kmpquiz.domain.usecase.qanda.DeleteQandasUseCase
import ygmd.kmpquiz.domain.usecase.qanda.GetQandaUseCase
import ygmd.kmpquiz.domain.usecase.qanda.QandaEditUseCase
import ygmd.kmpquiz.domain.usecase.qanda.SaveQandasUseCase
import ygmd.kmpquiz.domain.usecase.quiz.DeleteQuizUseCase
import ygmd.kmpquiz.domain.usecase.quiz.GetQuizUseCase
import ygmd.kmpquiz.domain.usecase.quiz.SaveQuizUseCase
import ygmd.kmpquiz.domain.usecase.quizSession.QuizSessionUseCase

// Domain Layer - Use Cases
val domainModule = module {
    factory {
        FetchUseCase(
            fetchRepository = get(),
        )
    }

    factory {
        GetQandaUseCase(
            qandaRepository = get()
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
        QuizSessionUseCase(
            quizSessionRepository = get(),
            qandaRepository = get(),
            quizRepository = get()
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
            scheduleQuizUseCase = get(),
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

    single<TimeProvider> {
        object : TimeProvider {}
    }
}
