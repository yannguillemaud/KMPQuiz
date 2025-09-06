package ygmd.kmpquiz.domain.entities.quiz

import ygmd.kmpquiz.domain.entities.cron.QuizCron
import ygmd.kmpquiz.domain.entities.qanda.Qanda

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
