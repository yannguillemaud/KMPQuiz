package koin

import org.koin.dsl.module
import ygmd.kmpquiz.viewModel.fetch.FetchQandasViewModel
import ygmd.kmpquiz.viewModel.quiz.QuizViewModel
import ygmd.kmpquiz.viewModel.quiz.session.QuizSessionViewModel
import ygmd.kmpquiz.viewModel.save.SavedQandasViewModel
import ygmd.kmpquiz.viewModel.settings.NotificationSettingsViewModel
import kotlin.time.ExperimentalTime

// Presentation Layer - ViewModels
@ExperimentalTime
val viewModelModule = module {
    factory {
        FetchQandasViewModel(
            fetchQandaUseCase = get(),
            saveQandasUseCase = get(),
            getFetchQandasUseCase = get(),
            getSavedQandasUseCase = get(),
            saveFetchQandasUseCase = get(),
            createQuizUseCase = get(),
        )
    }

    factory {
        SavedQandasViewModel(
            getQandaUseCase = get(),
            deleteQandasUseCase = get()
        )
    }

    factory {
        QuizSessionViewModel(
            startQuizSessionUseCase = get(),
            getQuizUseCase = get()
        )
    }

    factory {
        QuizViewModel(
            quizRepository = get(),
            getQandaUseCase = get(),
            createQuizUseCase = get()
        )
    }

    factory {
        NotificationSettingsViewModel(
            getQuizUseCase = get(),
            updateQuizUseCase = get(),
            rescheduleTasksUseCase = get()
        )
    }
}