package ygmd.kmpquiz.domain.cron

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ygmd.kmpquiz.domain.cron.CronExpression.CronFactory.CronBuilder

class CronExpressionTest {

    @Test
    fun `parse valid cron expression`() {
        val cron = CronExpression.parse("0 9 1 1")
        assertEquals("0", cron.minute)
        assertEquals("9", cron.hour)
        assertEquals("1", cron.day)
        assertEquals("1", cron.month)
    }

    @Test
    fun `parse with wildcards`() {
        val cron = CronExpression.parse("* * * *")
        assertEquals("*", cron.minute)
        assertEquals("*", cron.hour)
        assertEquals("*", cron.day)
        assertEquals("*", cron.month)
    }

    @Test
    fun `parse mixed wildcards and values`() {
        val cron = CronExpression.parse("30 14 * 6")
        assertEquals("30", cron.minute)
        assertEquals("14", cron.hour)
        assertEquals("*", cron.day)
        assertEquals("6", cron.month)
    }

    @Test
    fun `toString returns correct format`() {
        val cron = CronExpression.parse("15 8 25 12")
        assertEquals("15 8 25 12", cron.toString())
    }

    // Tests de validation des ranges
    @Test
    fun `validate minute range - valid values`() {
        CronExpression.parse("0 * * *")  // Min
        CronExpression.parse("59 * * *") // Max
    }

    @Test
    fun `validate minute range - invalid values`() {
        assertThrows<IllegalArgumentException> {
            CronExpression.parse("-1 * * *")
        }
        assertThrows<IllegalArgumentException> {
            CronExpression.parse("60 * * *")
        }
    }

    @Test
    fun `validate hour range - valid values`() {
        CronExpression.parse("* 0 * *")  // Min
        CronExpression.parse("* 23 * *") // Max
    }

    @Test
    fun `validate hour range - invalid values`() {
        assertThrows<IllegalArgumentException> {
            CronExpression.parse("* -1 * *")
        }
        assertThrows<IllegalArgumentException> {
            CronExpression.parse("* 24 * *")
        }
    }

    @Test
    fun `validate day range - valid values`() {
        CronExpression.parse("* * 1 *")  // Min
        CronExpression.parse("* * 31 *") // Max
    }

    @Test
    fun `validate day range - invalid values`() {
        assertThrows<IllegalArgumentException> {
            CronExpression.parse("* * 0 *")
        }
        assertThrows<IllegalArgumentException> {
            CronExpression.parse("* * 32 *")
        }
    }

    @Test
    fun `validate month range - valid values`() {
        CronExpression.parse("* * * 1")  // Min
        CronExpression.parse("* * * 12") // Max
    }

    @Test
    fun `validate month range - invalid values`() {
        assertThrows<IllegalArgumentException> {
            CronExpression.parse("* * * 0")
        }
        assertThrows<IllegalArgumentException> {
            CronExpression.parse("* * * 13")
        }
    }

    // Tests de format
    @Test
    fun `reject invalid format - too few parts`() {
        assertThrows<IllegalArgumentException> {
            CronExpression.parse("0 9 1")
        }
    }

    @Test
    fun `reject invalid format - too many parts`() {
        assertThrows<IllegalArgumentException> {
            CronExpression.parse("0 9 1 1 5")
        }
    }

    @Test
    fun `reject invalid format - empty string`() {
        assertThrows<IllegalArgumentException> {
            CronExpression.parse("")
        }
    }

    @Test
    fun `reject non-numeric values`() {
        assertThrows<IllegalArgumentException> {
            CronExpression.parse("abc * * *")
        }
    }

    // Tests avec CronBuilder
    @Test
    fun `builder with valid values`() {
        val cron = CronBuilder().apply {
            minute = "30"
            hour = "14"
            day = "15"
            month = "6"
        }.build()

        assertEquals("30 14 15 6", cron.toString())
    }

    @Test
    fun `builder with invalid minute`() {
        assertThrows<IllegalArgumentException> {
            CronBuilder().apply {
                minute = "60"
            }
        }
    }

    @Test
    fun `builder with wildcards`() {
        val cron = CronBuilder().build()
        assertEquals("* * * *", cron.toString())
    }

    // Tests edge cases
    @Test
    fun `handle extra whitespace`() {
        assertThrows<IllegalArgumentException> {
            CronExpression.parse("  0   9   1   1  ")
        }
    }

    @Test
    fun `handle empty fields`() {
        assertThrows<IllegalArgumentException> {
            CronExpression.parse("0  1 1")
        }
    }
}