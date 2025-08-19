package ygmd.kmpquiz.domain.entities.quiz

import ygmd.kmpquiz.domain.entities.cron.QuizCron
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import kotlin.time.Clock
import kotlin.time.Instant

data class Quiz (
    val id: String,
    val title: String,
    val qandas: List<Qanda>,
    val quizCron: QuizCron? = null,
    val createdAt: Instant = Clock.System.now()
)

data class QuizDraft(
    val title: String,
    val qandas: List<Qanda>,
    val QuizCron: QuizCron? = null,
)
