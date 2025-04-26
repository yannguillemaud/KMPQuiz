package ygmd.kmpquiz.domain.viewModel.displayable

import ygmd.kmpquiz.domain.model.cron.QuizCron
import ygmd.kmpquiz.domain.model.quiz.Quiz

data class DisplayableQuiz(
    val id: String,
    val title: String,
    val description: String? = null,
    val questionsSize: Int,
    val cron: QuizCron? = null,
)

fun Quiz.displayable(): DisplayableQuiz =
    DisplayableQuiz(
        id = id,
        title = title,
        description = null,
        questionsSize = qandas.size,
        cron = quizCron,
    )