package ygmd.kmpquiz.domain.model.cron

data class QuizCron(
    val cron: CronExpression,
    val isEnabled: Boolean = true,
)

data class CronExpression(val expression: String, val displayName: String)