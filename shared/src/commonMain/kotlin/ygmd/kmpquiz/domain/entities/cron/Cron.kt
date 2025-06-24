package ygmd.kmpquiz.domain.entities.cron

data class Cron(
    val id: String,
    val expression: CronExpression,
    val category: String? = null,
    val qandaId: Long? = null,
    val isActive: Boolean = true,
){
    val expressionAsText: String = expression.expression
}

/** EXEMPLES D'USAGE **/
fun main() {
    // Cron global : "Tous les jours à 9h pour tous les quiz"
    Cron(
        id = "global_daily",
        expression = CronExpression.parse("0 9 * * *"),
        category = null,
        qandaId = null
    )

    // Cron par catégorie : "Science tous les 2 jours"
    Cron(
        id = "science_2days",
        expression = CronExpression.parse("0 9 */2 * *"),
        category = "Science",
        qandaId = null
    )

    // Cron spécifique : "Ce quiz précis une fois par semaine"
    Cron(
        id = "quiz_123_weekly",
        expression = CronExpression.parse("0 9 * * 1"),
        category = null,
        qandaId = 123L
    )
}