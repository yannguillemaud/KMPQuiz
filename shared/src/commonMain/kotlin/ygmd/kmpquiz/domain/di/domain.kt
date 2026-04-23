package ygmd.kmpquiz.domain.di

import org.koin.dsl.module
import ygmd.kmpquiz.domain.usecase.category.CategoryUseCase
import ygmd.kmpquiz.domain.usecase.cron.CronUseCase
import ygmd.kmpquiz.domain.usecase.fetch.FetchUseCase
import ygmd.kmpquiz.domain.usecase.notification.ScheduleAllQuizzesUseCase
import ygmd.kmpquiz.domain.usecase.qanda.DeleteQandasUseCase
import ygmd.kmpquiz.domain.usecase.qanda.GetQandaUseCase
import ygmd.kmpquiz.domain.usecase.qanda.QandaEditUseCase
import ygmd.kmpquiz.domain.usecase.qanda.SaveQandasUseCase
import ygmd.kmpquiz.domain.usecase.quiz.DeleteQuizUseCase
import ygmd.kmpquiz.domain.usecase.quiz.GetQuizUseCase
import ygmd.kmpquiz.domain.usecase.quiz.QuizUseCase
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
            taskScheduler = get()
        )
    }

    factory {
        CronUseCase(
            quizRepository = get(),
            cronRepository = get(),
            taskScheduler = get(),
        )
    }

    factory {
        ScheduleAllQuizzesUseCase(
            taskScheduler = get(),
            quizRepository = get(),
        )
    }

    factory {
        QuizUseCase(
            quizRepository = get(),
            taskScheduler = get(),
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
