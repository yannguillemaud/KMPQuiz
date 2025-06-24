package ygmd.kmpquiz.domain.entities.cron

enum class CronPreset(private val expression: String, val displayName: String) {
    // Fréquences communes
    DAILY("0 9 * * *", "Tous les jours à 9h"),
    WEEKLY("0 9 * * 1", "Tous les lundis à 9h"),
    MONTHLY("0 9 1 * *", "Le 1er de chaque mois à 9h"),

    // Fréquences d'étude
    TWICE_DAILY("0 9,18 * * *", "Matin (9h) et soir (18h)"),
    WEEKDAYS("0 9 * * 1-5", "En semaine à 9h"),
    WEEKENDS("0 10 * * 0,6", "Week-ends à 10h"),

    // Révisions espacées (Spaced Repetition)
    EVERY_3_DAYS("0 9 */3 * *", "Tous les 3 jours à 9h"),
    WEEKLY_REVIEW("0 19 * * 5", "Révision hebdo (vendredi 19h)"),

    // tests/debug
    EVERY_MINUTE("* * * * *", "Toutes les minutes (test)"),
    HOURLY("0 * * * *", "Toutes les heures");

    fun toCronExpression(): CronExpression = CronExpression.parse(expression)
}