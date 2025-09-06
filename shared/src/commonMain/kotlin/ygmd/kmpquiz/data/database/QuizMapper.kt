package ygmd.kmpquiz.data.database

import ygmd.kmpquiz.domain.entities.quiz.DraftQuiz

data class QuizInsert(
    val title: String,
    val descritpion: String?,
    val cronExpression: String?,
    val cronTitle: String?,
)

fun DraftQuiz.toQuizInsert() = QuizInsert(
    title = title,
    descritpion = description,
    cronExpression = cron?.cron?.expression,
    cronTitle = cron?.cron?.displayName,
)