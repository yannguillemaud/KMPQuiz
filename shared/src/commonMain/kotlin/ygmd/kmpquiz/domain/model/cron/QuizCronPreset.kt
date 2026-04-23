package ygmd.kmpquiz.domain.model.cron

import com.ucasoft.kcron.core.extensions.anyDays
import com.ucasoft.kcron.core.extensions.anyHours
import com.ucasoft.kcron.core.extensions.anyMonths
import com.ucasoft.kcron.core.extensions.anyYears
import com.ucasoft.kcron.core.extensions.at
import com.ucasoft.kcron.core.extensions.days
import com.ucasoft.kcron.core.extensions.hours
import com.ucasoft.kcron.core.extensions.minutes
import com.ucasoft.kcron.core.extensions.seconds
import com.ucasoft.kcron.cron
import java.util.UUID

enum class QuizCronPreset(
    val id: String = UUID.randomUUID().toString(),
    val expression: String,
    val displayName: String
) {
    DAILY(
        expression = cron {
            seconds(0)
            minutes(0)
            hours(0)
            anyDays()
            anyMonths()
            anyYears()
        }.expression,
        displayName = "Daily"
    ),

    HOURLY(
        expression = cron {
            seconds(0)
            minutes(0)
            hours(0)
            anyDays()
            anyMonths()
            anyYears()
        }.expression,
        displayName = "Hourly"
    ),

    WEEKLY(
        expression = cron {
            seconds(0)
            minutes(0)
            hours(0)
            days(values = intArrayOf(6, 7))
            anyMonths()
            anyYears()
        }.expression,
        displayName = "Weekly"
    ),

    DEBUG(
        expression = cron {
            seconds(0)
            minutes(15 at 0)
            anyHours()
            anyDays()
            anyMonths()
            anyYears()
        }.expression,
        displayName = "Debug"
    )
}