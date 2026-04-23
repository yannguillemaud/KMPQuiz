package ygmd.kmpquiz.domain.model.quiz

import ygmd.kmpquiz.domain.model.category.Category
import ygmd.kmpquiz.domain.model.cron.QuizCron

data class Quiz (
    val id: String,
    val title: String,
    val categories: List<Category> = emptyList(),
    val cron: QuizCron? = null,
    val config: QuizConfigDetails,
    val questionsCount: Int,
)