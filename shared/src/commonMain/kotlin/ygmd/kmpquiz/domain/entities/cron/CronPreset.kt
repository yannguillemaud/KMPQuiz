package ygmd.kmpquiz.domain.entities.cron

import com.ucasoft.kcron.Cron.builder
import com.ucasoft.kcron.core.extensions.anyDays
import com.ucasoft.kcron.core.extensions.anyHours
import com.ucasoft.kcron.core.extensions.at
import com.ucasoft.kcron.core.extensions.daysOfWeek
import com.ucasoft.kcron.core.extensions.hours
import com.ucasoft.kcron.core.extensions.minutes
import com.ucasoft.kcron.core.extensions.on
import com.ucasoft.kcron.core.extensions.seconds
import com.ucasoft.kcron.core.extensions.years

enum class CronPreset(private val expression: String, val displayName: String) {
    // Fréquences communes
    DAILY(
        expression = builder().apply {
            anyDays()
            hours(9)
        }.expression,
        displayName = "Tous les jours à 9h"
    ),
    HOURLY(
        expression = builder().apply {
            anyHours()
        }.expression,
        displayName = "Toutes les heures"
    ),
    WEEKLY(
        expression = builder().apply {
            daysOfWeek(1)
        }.expression,
        displayName = "Toutes les semaines"
    ),

    // tests/debug
    EVERY_MINUTE("* * * * *", "Toutes les minutes (test)"),

    TEST_CRON_LIB(
        expression = builder().apply {
            seconds(10 at 0)
            minutes(5..25)
            hours(5, 12)
            daysOfWeek(7 on 5)
            years(2050)

        }.expression,
        displayName = "Cron Personnalisé par lib"
    );

    fun toCronExpression(): CronExpression = CronExpression(expression, displayName)
}