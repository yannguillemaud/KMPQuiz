package ygmd.kmpquiz.domain.entities.cron

/**
 * Format: "minute hour day month dayOfWeek"
 */
data class CronExpression(val expression: String) {
    companion object {
        fun parse(expression: String): CronExpression {
            val parts = expression.trim().split(" ")
            require(parts.size == 5) { "Cron expression must have 5 parts: $expression" }

            parts.forEach { part ->
                require(part.matches(Regex("[0-9*,/-]+")) || part == "*") {
                    "Invalid cron part: $part"
                }
            }

            return CronExpression(expression.trim())
        }

        fun of(minute: String = "*", hour: String = "*", day: String = "*",
               month: String = "*", dayOfWeek: String = "*"): CronExpression {
            return CronExpression("$minute $hour $day $month $dayOfWeek")
        }
    }

    override fun toString(): String = expression
}