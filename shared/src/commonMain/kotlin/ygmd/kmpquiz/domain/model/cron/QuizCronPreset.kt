package ygmd.kmpquiz.domain.model.cron

import com.ucasoft.kcron.Cron.builder
import com.ucasoft.kcron.core.extensions.anyDays
import com.ucasoft.kcron.core.extensions.anyHours
import com.ucasoft.kcron.core.extensions.at
import com.ucasoft.kcron.core.extensions.days
import com.ucasoft.kcron.core.extensions.hours
import com.ucasoft.kcron.core.extensions.minutes
import com.ucasoft.kcron.core.extensions.seconds

enum class QuizCronPreset(private val expression: String, val displayName: String) {
    // Fréquences communes
    DAILY(
        expression = builder().apply {
            seconds(0)
            minutes(0)
            hours(0)
            anyDays()
        }.expression,
        displayName = "Daily"
    ),
    HOURLY(
        expression = builder().apply {
            seconds(0)
            minutes(0)
            anyHours()
            anyDays()
        }.expression,
        displayName = "Hourly"
    ),
    WEEKLY(
        expression = builder().apply {
            seconds(0)
            minutes(0)
            hours(0)
            days(values = intArrayOf(6, 7))
        }.expression,
        displayName = "Weekly"
    ),

    // tests/debug
    EVERY_15_MINUTES(
        expression = builder().apply {
            seconds(0)
            minutes(15 at 0)
            anyHours()
            anyDays()
        }.expression,
        displayName = "Every 15 minutes"
    );

    fun toCronExpression(): CronExpression = CronExpression(expression, displayName)
}