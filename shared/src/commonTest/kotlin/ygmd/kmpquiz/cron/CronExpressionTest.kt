package ygmd.kmpquiz.cron

import ygmd.kmpquiz.domain.entities.cron.CronExpression
import ygmd.kmpquiz.domain.entities.cron.CronExpression.CronFactory.CronBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CronExpressionTest {

    @Test
    fun `parse valid cron expression`() {
        val cron = CronExpression.parse("0 9 1 1 0")
        assertEquals("0", cron.minute)
        assertEquals("9", cron.hour)
        assertEquals("1", cron.day)
        assertEquals("1", cron.month)
        assertEquals("0", cron.dayOfWeek)
    }

    @Test
    fun `parse with wildcards`() {
        val cron = CronExpression.parse("* * * * *")
        assertEquals("*", cron.minute)
        assertEquals("*", cron.hour)
        assertEquals("*", cron.day)
        assertEquals("*", cron.month)
        assertEquals("*", cron.dayOfWeek)
    }

    @Test
    fun `parse mixed wildcards and values`() {
        val cron = CronExpression.parse("30 14 * 6 1")
        assertEquals("30", cron.minute)
        assertEquals("14", cron.hour)
        assertEquals("*", cron.day)
        assertEquals("6", cron.month)
        assertEquals("1", cron.dayOfWeek)
    }

    @Test
    fun `toString returns correct format`() {
        val cron = CronExpression.parse("15 8 25 12 6")
        assertEquals("15 8 25 12 6", cron.toString())
    }

    @Test
    fun `validate minute range - valid values`() {
        CronExpression.parse("0 * * * *")
        CronExpression.parse("59 * * * *")
    }

    @Test
    fun `validate minute range - invalid values`() {
        assertFailsWith<IllegalArgumentException> {
            CronExpression.parse("-1 * * * *")
        }
        assertFailsWith<IllegalArgumentException> {
            CronExpression.parse("60 * * * *")
        }
    }

    @Test
    fun `validate hour range - valid values`() {
        CronExpression.parse("* 0 * * *")
        CronExpression.parse("* 23 * * *")
    }

    @Test
    fun `validate hour range - invalid values`() {
        assertFailsWith<IllegalArgumentException> {
            CronExpression.parse("* -1 * * *")
        }
        assertFailsWith<IllegalArgumentException> {
            CronExpression.parse("* 24 * * *")
        }
    }

    @Test
    fun `validate day range - valid values`() {
        CronExpression.parse("* * 1 * *")
        CronExpression.parse("* * 31 * *")
    }

    @Test
    fun `validate day range - invalid values`() {
        assertFailsWith<IllegalArgumentException> {
            CronExpression.parse("* * 0 * *")
        }
        assertFailsWith<IllegalArgumentException> {
            CronExpression.parse("* * 32 * *")
        }
    }

    @Test
    fun `validate month range - valid values`() {
        CronExpression.parse("* * * 1 *")
        CronExpression.parse("* * * 12 *")
    }

    @Test
    fun `validate month range - invalid values`() {
        assertFailsWith<IllegalArgumentException> {
            CronExpression.parse("* * * 0 *")
        }
        assertFailsWith<IllegalArgumentException> {
            CronExpression.parse("* * * 13 *")
        }
    }

    @Test
    fun `validate day of week range - valid values`() {
        CronExpression.parse("* * * * 0")  // Sunday
        CronExpression.parse("* * * * 1")  // Monday
        CronExpression.parse("* * * * 6")  // Saturday
        CronExpression.parse("* * * * 7")  // Sunday (alternative)
    }

    @Test
    fun `validate day of week range - invalid values`() {
        assertFailsWith<IllegalArgumentException> {
            CronExpression.parse("* * * * -1")
        }
        assertFailsWith<IllegalArgumentException> {
            CronExpression.parse("* * * * 8")
        }
    }

    @Test
    fun `reject invalid format - too few parts`() {
        assertFailsWith<IllegalArgumentException> {
            CronExpression.parse("0 9 1 1")
        }
    }

    @Test
    fun `reject invalid format - too many parts`() {
        assertFailsWith<IllegalArgumentException> {
            CronExpression.parse("0 9 1 1 5 6")
        }
    }

    @Test
    fun `reject invalid format - empty string`() {
        assertFailsWith<IllegalArgumentException> {
            CronExpression.parse("")
        }
    }

    @Test
    fun `reject non-numeric values`() {
        assertFailsWith<IllegalArgumentException> {
            CronExpression.parse("abc * * * *")
        }
    }

    @Test
    fun `builder with valid values`() {
        val cron = CronBuilder().apply {
            minute = "30"
            hour = "14"
            day = "15"
            month = "6"
            dayOfWeek = "2"
        }.build()

        assertEquals("30 14 15 6 2", cron.toString())
    }

    @Test
    fun `builder with invalid minute`() {
        assertFailsWith<IllegalArgumentException> {
            CronBuilder().apply {
                minute = "60"
            }
        }
    }

    @Test
    fun `builder with invalid day of week`() {
        assertFailsWith<IllegalArgumentException> {
            CronBuilder().apply {
                dayOfWeek = "8"
            }
        }
    }

    @Test
    fun `builder with wildcards`() {
        val cron = CronBuilder().build()
        assertEquals("* * * * *", cron.toString())
    }

    @Test
    fun `handle extra whitespace`() {
        assertFailsWith<IllegalArgumentException> {
            CronExpression.parse("  0   9   1   1   0  ")
        }
    }

    @Test
    fun `handle empty fields`() {
        assertFailsWith<IllegalArgumentException> {
            CronExpression.parse("0  1 1 *")
        }
    }

    @Test
    fun `parse cron with different day of week formats`() {
        // Test Sunday as 0
        val cronSunday0 = CronExpression.parse("0 8 * * 0")
        assertEquals("0", cronSunday0.dayOfWeek)

        // Test Sunday as 7 (alternative)
        val cronSunday7 = CronExpression.parse("0 8 * * 7")
        assertEquals("7", cronSunday7.dayOfWeek)

        // Test Monday
        val cronMonday = CronExpression.parse("0 8 * * 1")
        assertEquals("1", cronMonday.dayOfWeek)

        // Test Saturday
        val cronSaturday = CronExpression.parse("0 8 * * 6")
        assertEquals("6", cronSaturday.dayOfWeek)
    }
}