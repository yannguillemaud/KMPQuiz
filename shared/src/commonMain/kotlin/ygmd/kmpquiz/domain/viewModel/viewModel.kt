package ygmd.kmpquiz.domain.viewModel

import org.koin.dsl.module
import ygmd.kmpquiz.domain.usecase.quizSession.ObserveSessionUseCase
import ygmd.kmpquiz.domain.viewModel.category.CategoryViewModel
import ygmd.kmpquiz.domain.viewModel.fetch.HomeViewModel
import ygmd.kmpquiz.domain.viewModel.permission.PermissionViewModel
import ygmd.kmpquiz.domain.viewModel.qandas.saved.CategoryQandaViewModel
import ygmd.kmpquiz.domain.viewModel.quiz.QuizViewModel
import ygmd.kmpquiz.domain.viewModel.quiz.edit.QuizEditViewModel
import ygmd.kmpquiz.domain.viewModel.quiz.session.DetailedSessionViewModel
import ygmd.kmpquiz.domain.viewModel.quiz.session.QuizSessionViewModel
import ygmd.kmpquiz.domain.viewModel.quiz.session.SessionViewModel

val viewModelModule = module {
    factory {
        HomeViewModel(
            fetchQandaUseCase = get(),
            saveQandaUseCase = get(),
            categoryUseCase = get(),
            qandaUseCase = get(),
            quizUseCase = get(),
        )
    }

    factory {
        CategoryQandaViewModel(
            savedStateHandle = get(),
            categoryUseCase = get(),
            getQandasUseCase = get(),
        )
    }

    factory {
        QuizSessionViewModel(
            getQuizUC = get(),
            savedStateHandle = get(),
            setUpSessionUC = get(),
            submitAnswer = get(),
            observeSessionUC = get(),
            nextState = get(),
            getDetailedQandaUC = get(),
        )
    }

    factory {
        ObserveSessionUseCase(
            sessionRepository = get(),
        )
    }

    factory {
        QuizViewModel(
            getQuizUseCase = get(),
            deleteQuizUseCase = get(),
            toggleQuizSchedulerUseCase = get(),
            grantManager = get(),
        )
    }

    factory {
        QuizEditViewModel(
            saveQuizUseCase = get(),
            categoryUseCase = get(),
            getQuizUseCase = get(),
            grantManager = get(),
            savedStateDelegate = get(),
        )
    }

    factory {
        CategoryViewModel(
            categoryUseCase = get(),
        )
    }

    factory {
        PermissionViewModel(
            grantManager = get(),
        )
    }

    factory {
        SessionViewModel(
            getSessionsUseCase = get(),
            getQuizUseCase = get(),
        )
    }

    factory {
        DetailedSessionViewModel(
            getSessionsUseCase = get(),
            getQuizUseCase = get(),
            sessionResultsUseCase = get(),
            getQandaUseCase = get(),
            categoryUseCase = get(),
        )
    }
}