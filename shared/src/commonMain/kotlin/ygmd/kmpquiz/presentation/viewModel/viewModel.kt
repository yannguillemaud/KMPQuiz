package ygmd.kmpquiz.presentation.viewModel

import org.koin.dsl.module
import ygmd.kmpquiz.core.usecase.session.ObserveSessionUseCase
import ygmd.kmpquiz.presentation.viewModel.category.CategoryViewModel
import ygmd.kmpquiz.presentation.viewModel.home.HomeViewModel
import ygmd.kmpquiz.presentation.viewModel.category.CategoryQandaViewModel
import ygmd.kmpquiz.presentation.viewModel.quiz.QuizViewModel
import ygmd.kmpquiz.presentation.viewModel.quiz.edit.QuizEditViewModel
import ygmd.kmpquiz.presentation.viewModel.quiz.session.CategoryReviewViewModel
import ygmd.kmpquiz.presentation.viewModel.quiz.session.DetailedSessionViewModel
import ygmd.kmpquiz.presentation.viewModel.quiz.session.QuizSessionViewModel
import ygmd.kmpquiz.presentation.viewModel.quiz.session.SessionViewModel

val viewModelModule = module {
    factory {
        HomeViewModel(
            categoryUseCase = get(),
            getQandaUseCase = get(),
            getQuizUseCase = get(),
            getFetchersUseCase = get(),
            downloadQandasUseCase = get(),
        )
    }

    factory { (categoryId: String) ->
        CategoryQandaViewModel(
            categoryId = categoryId,
            categoryUseCase = get(),
            getQandasUseCase = get(),
        )
    }

    factory {
        QuizSessionViewModel(
            getQuizUC = get(),
            setUpSessionUC = get(),
            submitAnswer = get(),
            observeSessionUC = get(),
            nextState = get(),
            getDetailedQandaUC = get(),
            sessionResultsUseCase = get(),
            categoryUseCase = get(),
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
        )
    }

    factory {
        CategoryViewModel(
            categoryUseCase = get(),
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
            sessionQuestionReviewUseCase = get(),
            categoryUseCase = get(),
        )
    }

    factory {
        CategoryReviewViewModel(
            observeSessionUseCase = get(),
            sessionResultsUseCase = get(),
            sessionQuestionReviewUseCase = get(),
        )
    }
}