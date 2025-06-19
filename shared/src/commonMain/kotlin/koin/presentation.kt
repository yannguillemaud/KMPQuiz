package koin

import org.koin.dsl.module
import ygmd.kmpquiz.viewModel.NotificationTestViewModel
import ygmd.kmpquiz.viewModel.fetch.FetchQandasViewModel
import ygmd.kmpquiz.viewModel.quiz.QuizViewModel
import ygmd.kmpquiz.viewModel.save.SavedQandasViewModel
import ygmd.kmpquiz.viewModel.settings.NotificationSettingsViewModel
import ygmd.kmpquiz.viewModel.settings.SettingsViewModel

// Presentation Layer - ViewModels
val presentationModule = module {
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
        SettingsViewModel(
            getQandasUseCase = get(),
            notificationConfigRepository = get()
        )
    }

    factory {
        QuizViewModel(
            quizUseCase = get(),
            logger = get()
        )
    }

    factory {
        NotificationTestViewModel(
            notificationUseCase = get()
        )
    }

    factory {
        NotificationSettingsViewModel(
            configRepository = get(),
            getQandasUseCase = get(),
            notificationUseCase = get()
        )
    }
}