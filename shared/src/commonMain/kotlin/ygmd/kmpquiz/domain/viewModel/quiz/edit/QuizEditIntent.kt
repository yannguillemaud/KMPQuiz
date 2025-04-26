package ygmd.kmpquiz.domain.viewModel.quiz.edit

import ygmd.kmpquiz.domain.model.cron.CronExpression
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableCategory

interface QuizEditIntent {
    data class Load(val quizId: String) : QuizEditIntent
    data class UpdateTitle(val title: String) : QuizEditIntent
    data class UpdateCategories(val categories: List<DisplayableCategory>) : QuizEditIntent
    data class UpdateCron(val cron: CronExpression?) : QuizEditIntent
    object Save : QuizEditIntent
}