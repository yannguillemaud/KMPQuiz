package koin

import org.koin.dsl.module
import ygmd.kmpquiz.viewModel.fetch.FetchQandasViewModel
import ygmd.kmpquiz.viewModel.quiz.QuizViewModel
import ygmd.kmpquiz.viewModel.save.SavedQandasViewModel
import ygmd.kmpquiz.viewModel.settings.NotificationSettingsViewModel

// Presentation Layer - ViewModels
val viewModelModule = module {
    factory {
        FetchQandasViewModel(
            fetchQandaUseCase = get(),
            getQandasUseCase = get()
        )
    }

    factory {
        SavedQandasViewModel(
            getQandasUseCase = get(),
            saveQandaUseCase = get(),
            deleteQandasUseCase = get()
        )
    }

    factory {
        QuizViewModel(
            quizUseCase = get(),
            logger = get()
        )
    }

    factory {
        NotificationSettingsViewModel(
            getQandasUseCase = get(),
            cronUseCase = get(),
            notificationUseCase = get()
        )
    }
}