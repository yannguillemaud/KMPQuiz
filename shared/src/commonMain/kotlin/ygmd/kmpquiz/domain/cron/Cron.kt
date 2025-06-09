package ygmd.kmpquiz.domain.cron

class CronExpression private constructor(
    val minute: String = "*",
    val hour: String = "*",
    val day: String = "*",
    val month: String = "*",
) {
    companion object CronFactory {
        class CronBuilder {
            var minute: String = "*"
                set(value) { field = value; validateField(value, MINUTES_RANGE, "minutes") }
            var hour: String = "*"
                set(value) { field = value; validateField(value, HOURS_RANGE, "hours") }
            var day: String = "*"
                set(value) { field = value; validateField(value, DAYS_RANGE, "days") }
            var month: String = "*"
                set(value) { field = value; validateField(value, MONTHS_RANGE, "months") }

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

            private fun isValidCronExpression(value: String, range: LongRange): Boolean {
                return value.matches(Regex("[0-9,\\-/\\*]+"))
            }

            private val MINUTES_RANGE = 0L..59
            private val HOURS_RANGE = 0L..23
            private val DAYS_RANGE = 1L..31
            private val MONTHS_RANGE = 1L..12

            fun build(): CronExpression = CronExpression(
                minute, hour, day, month
            )
        }

        fun parse(expression: String): CronExpression {
            val parts = expression.split(" ")
            if(parts.size != 4) throw IllegalArgumentException("Invalid format: $expression")
            return CronBuilder().apply {
                minute = parts[0]
                hour = parts[1]
                day = parts[2]
                month = parts[3]
            }.build()
        }
    }

    override fun toString(): String = "$minute $hour $day $month"
}