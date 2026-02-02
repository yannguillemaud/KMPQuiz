package ygmd.kmpquiz.domain.viewModel

import org.koin.dsl.module
import ygmd.kmpquiz.domain.viewModel.category.CategoryViewModel
import ygmd.kmpquiz.domain.viewModel.fetch.FetchQandasViewModel
import ygmd.kmpquiz.domain.viewModel.qandas.edit.QandaEditViewModel
import ygmd.kmpquiz.domain.viewModel.qandas.saved.QandaOfCategoryViewModel
import ygmd.kmpquiz.domain.viewModel.quiz.QuizViewModel
import ygmd.kmpquiz.domain.viewModel.quiz.edit.QuizEditViewModel
import ygmd.kmpquiz.domain.viewModel.quiz.session.QuizSessionViewModel


val viewModelModule = module {
    factory {
        FetchQandasViewModel(
            getFetchersUseCase = get(),
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
            getQuizUseCase = get(),
            quizSessionUseCase = get(),
            categoryUseCase = get(),
        )
    }

    factory {
        QuizViewModel(
            getQuizUseCase = get(),
            deleteQuizUseCase = get(),
            toggleCronUseCase = get(),
            rescheduleTasksUseCase = get(),
        )
    }

    factory {
        QuizEditViewModel(
            quizEditUseCase = get(),
            categoryUseCase = get(),
            rescheduleTasksUseCase = get()
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
}