package ygmd.kmpquiz.domain.entities.cron

enum class CronPreset(val expression: String) {
    EVERY_MINUTE("* * * * *"),
    HOURLY("0 * * * *"),
    DAILY("0 0 * * *"),
    WEEKLY("0 0 * * 0"),
    MONTHLY("0 0 1 * *"),
    YEARLY("0 0 1 1 *");

    fun toCronExpression(): CronExpression {
        return CronExpression.parse(expression)
    }
}