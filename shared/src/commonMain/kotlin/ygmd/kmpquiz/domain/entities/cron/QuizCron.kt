package ygmd.kmpquiz.domain.entities.cron

data class QuizCron(
    val cron: CronExpression,
    val isEnabled: Boolean = true,
    val isGlobal: Boolean = false,
)

data class CronExpression(val expression: String, val displayName: String)