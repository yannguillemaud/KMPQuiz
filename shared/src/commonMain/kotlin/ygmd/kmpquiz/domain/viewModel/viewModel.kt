package ygmd.kmpquiz.domain.viewModel

import org.koin.dsl.module
import ygmd.kmpquiz.domain.viewModel.fetch.FetchQandasViewModel
import ygmd.kmpquiz.domain.viewModel.quiz.QuizViewModel
import ygmd.kmpquiz.domain.viewModel.quiz.session.QuizSessionViewModel
import ygmd.kmpquiz.domain.viewModel.save.SavedQandasViewModel
import ygmd.kmpquiz.domain.viewModel.settings.NotificationSettingsViewModel
import kotlin.time.ExperimentalTime

// Presentation Layer - ViewModels
@ExperimentalTime
val viewModelModule = module {
    factory {
        FetchQandasViewModel(
            fetchQandaUseCase = get(),
            getFetchQandasUseCase = get(),
        )
    }

    factory {
        SavedQandasViewModel(
            deleteFetchQandasUseCase = get(),
            deleteQandasUseCase = get(),
            saveQandaUseCase = get(),
            getQandaUseCase = get(),
        )
    }

    factory {
        QuizSessionViewModel(
            getQuizUseCase = get()
        )
    }

    factory {
        QuizViewModel(
            getQuizUseCase = get(),
            getQandaUseCase = get(),
            createQuizUseCase = get(),
            deleteQuizUseCase = get(),
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