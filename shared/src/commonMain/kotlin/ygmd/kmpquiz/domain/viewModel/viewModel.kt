package ygmd.kmpquiz.domain.viewModel

import org.koin.dsl.module
import ygmd.kmpquiz.domain.viewModel.category.CategoryViewModel
import ygmd.kmpquiz.domain.viewModel.fetch.FetchQandasViewModel
import ygmd.kmpquiz.domain.viewModel.permission.PermissionViewModel
import ygmd.kmpquiz.domain.viewModel.qandas.edit.QandaEditViewModel
import ygmd.kmpquiz.domain.viewModel.qandas.saved.QandaOfCategoryViewModel
import ygmd.kmpquiz.domain.viewModel.quiz.QuizViewModel
import ygmd.kmpquiz.domain.viewModel.quiz.edit.QuizEditViewModel
import ygmd.kmpquiz.domain.viewModel.quiz.session.QuizSessionViewModel

val viewModelModule = module {
    factory {
        FetchQandasViewModel(
            fetchQandaUseCase = get(),
            saveQandaUseCase = get()
        )
    }

    factory { (categoryId: String) ->
        QandaOfCategoryViewModel(
            categoryId = categoryId,
            deleteQandasUseCase = get(),
            saveQandaUseCase = get(),
            getQandaUseCase = get(),
            categoryUseCase = get(),
        )
    }

    factory { (quizId: String) ->
        QuizSessionViewModel(
            quizId = quizId,
            quizSessionUseCase = get(),
            categoryUseCase = get(),
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

    factory { (qandaId: String?) ->
        QandaEditViewModel(
            qandaId = qandaId,
            qandaEditUseCase = get(),
            categoryUseCase = get(),
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
}