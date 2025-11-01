package ygmd.kmpquiz.domain.model.quiz

import ygmd.kmpquiz.domain.model.cron.QuizCron
import ygmd.kmpquiz.domain.model.qanda.Qanda

data class Quiz (
    val id: String,
    val title: String,
    val qandas: List<Qanda>,
    val quizCron: QuizCron? = null,
)

data class DraftQuiz(
    val title: String,
    val qandas: List<Qanda>,
    val description: String? = null,
    val cron: QuizCron? = null,
)
