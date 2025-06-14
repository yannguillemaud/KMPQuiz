package ygmd.kmpquiz.domain.pojo.cron

class CronExpression private constructor(
    val minute: String = "*",
    val hour: String = "*",
    val day: String = "*",
    val month: String = "*",
    val dayOfWeek: String = "*"
) {
    companion object CronFactory {
        class CronBuilder {
            var minute: String = "*"
                set(value) {
                    field = value; validateField(value, MINUTES_RANGE, "minutes")
                }
            var hour: String = "*"
                set(value) {
                    field = value; validateField(value, HOURS_RANGE, "hours")
                }
            var day: String = "*"
                set(value) {
                    field = value; validateField(value, DAYS_RANGE, "days")
                }
            var month: String = "*"
                set(value) {
                    field = value; validateField(value, MONTHS_RANGE, "months")
                }
            var dayOfWeek: String = "*"
                set(value) {
                    field = value; validateDayOfWeekField(value)
                }

            private fun validateField(value: String, range: LongRange, fieldName: String) {
                if (value == "*") return

                try {
                    val numValue = value.toLong()
                    if (numValue !in range) {
                        throw IllegalArgumentException("$fieldName $value not in range $range")
                    }
                } catch (e: NumberFormatException) {
                    if (!isValidCronExpression(value, range)) {
                        throw IllegalArgumentException("Invalid $fieldName expression: $value")
                    }
                }
            }

            private fun validateDayOfWeekField(value: String) {
                if (value == "*") return

                try {
                    val numValue = value.toLong()
                    // Unix cron convention: 0-6 (0=Sunday, 6=Saturday) + 7 as Sunday alternative
                    if (numValue !in DAYS_OF_WEEK_RANGE && numValue != 7L) {
                        throw IllegalArgumentException("days of week $value not in range $DAYS_OF_WEEK_RANGE (7 also accepted for Sunday)")
                    }
                } catch (e: NumberFormatException) {
                    if (!isValidCronExpression(value, DAYS_OF_WEEK_RANGE)) {
                        throw IllegalArgumentException("Invalid days of week expression: $value")
                    }
                }
            }

            private fun isValidCronExpression(value: String, range: LongRange): Boolean {
                return value.matches(Regex("[0-9,\\-/*]+"))
            }

            // Standard Unix cron ranges
            private val MINUTES_RANGE = 0L..59
            private val HOURS_RANGE = 0L..23
            private val DAYS_RANGE = 1L..31        // Days of month: 1-31
            private val MONTHS_RANGE = 1L..12      // Months: 1-12 (January-December)
            private val DAYS_OF_WEEK_RANGE = 0L..6 // Days of week: 0-6 (Sunday-Saturday)

            fun build(): CronExpression = CronExpression(
                minute, hour, day, month, dayOfWeek
            )
        }

        fun parse(expression: String): CronExpression {
            val parts = expression.split(" ")
            if (parts.size != 5)
                throw IllegalArgumentException("Cron expression must have exactly 5 parts: $expression")

            return CronBuilder().apply {
                minute = parts[0]
                hour = parts[1]
                day = parts[2]
                month = parts[3]
                dayOfWeek = parts[4]
            }.build()
        }
    }

    override fun toString(): String = "$minute $hour $day $month $dayOfWeek"
}